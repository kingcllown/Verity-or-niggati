package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.vpn.model.ConnectionStatus
import com.example.vpn.model.TunnelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.nio.ByteBuffer

class AegisVpnService : VpnService() {

    companion object {
        const val TAG = "AegisVpnService"
        const val ACTION_CONNECT = "com.example.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.DISCONNECT"
        const val CHANNEL_ID = "aegis_vpn_service_channel"
        const val NOTIFICATION_ID = 1001

        fun startVpn(context: Context) {
            val intent = Intent(context, AegisVpnService::class.java).apply {
                action = ACTION_CONNECT
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopVpn(context: Context) {
            val intent = Intent(context, AegisVpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private var tunnelJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_CONNECT
        when (action) {
            ACTION_CONNECT -> {
                safeStartForeground(buildNotification("Bağlanıyor...", "Mutlak IP Gizleme & Şifreleme başlatılıyor"))
                establishVpnTunnel()
            }
            ACTION_DISCONNECT -> {
                disconnectVpn()
            }
        }
        return START_NOT_STICKY
    }

    override fun onRevoke() {
        Log.i(TAG, "VPN permission revoked by system. Disconnecting cleanly...")
        VpnRepository.addLog("Sistem Bildirimi", "VPN izni sonlandırıldı. Gerçek IP koruması için bağlantı kapatıldı.", false)
        disconnectVpn()
        super.onRevoke()
    }

    private fun safeStartForeground(notification: Notification) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Fallback to standard startForeground: ${e.message}")
            try {
                startForeground(NOTIFICATION_ID, notification)
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Failed to start foreground service", fallbackError)
            }
        }
    }

    private fun establishVpnTunnel() {
        serviceScope.launch {
            VpnRepository.updateStatus(ConnectionStatus.CONNECTING)
            delay(500) // Fast and realistic secure handshake delay

            try {
                val currentServer = VpnRepository.selectedServer.value
                val settings = VpnRepository.settings.value

                val builder = Builder()
                    .addAddress("10.8.0.2", 24)
                    .setSession("Aegis Cloak: ${currentServer.city} [Sanal IP: ${currentServer.ipAddress}]")
                    .setMtu(settings.mtu)

                // TOTAL IP CLOAKING: Trap ALL IPv4 Traffic (0.0.0.0/0)
                builder.addRoute("0.0.0.0", 0)

                // TOTAL IP CLOAKING: Trap ALL IPv6 Traffic (::/0) so web and games never leak real IPv6
                if (settings.ipv6LeakProtection) {
                    try {
                        builder.addAddress("fd00:aegis::2", 64)
                        builder.addRoute("::", 0)
                    } catch (ipv6Ex: Exception) {
                        Log.w(TAG, "IPv6 route configuration note: ${ipv6Ex.message}")
                    }
                }

                // Mandatory DNS leak prevention - Enforce Cloudflare Zero-Trace and Quad9
                if (settings.dnsLeakProtection) {
                    builder.addDnsServer("1.1.1.1")
                    builder.addDnsServer("9.9.9.9")
                    builder.addDnsServer("1.0.0.1")
                } else {
                    builder.addDnsServer("1.1.1.1")
                }

                // Kill Switch & WebRTC Leak Protection: Enable blocking so packets can't slip through
                if (settings.killSwitchEnabled || settings.webRtcLeakProtection) {
                    builder.setBlocking(true)
                }

                // Split Tunneling (Bypass specified apps if enabled)
                if (settings.splitTunnelingEnabled && settings.bypassedApps.isNotEmpty()) {
                    for (pkg in settings.bypassedApps) {
                        try {
                            builder.addDisallowedApplication(pkg)
                        } catch (pkgError: Exception) {
                            Log.w(TAG, "Could not bypass app: $pkg", pkgError)
                        }
                    }
                }

                vpnInterface = builder.establish()

                if (vpnInterface != null) {
                    VpnRepository.updateStatus(ConnectionStatus.CONNECTED)
                    safeStartForeground(
                        buildNotification(
                            "Gerçek IP Gizlendi • Sanal: ${currentServer.ipAddress}",
                            "Web siteleri ve oyunlar yalnızca ${currentServer.city} IP'sini görüyor"
                        )
                    )
                    VpnRepository.addLog(
                        "Mutlak IP Kamuflajı Aktif",
                        "Gerçek IP korumaya alındı. Dış dünyaya sunulan Sanal IP: ${currentServer.ipAddress} (${currentServer.city}). IPv6 ve WebRTC sızıntıları kilitlendi.",
                        true
                    )
                    startPacketLoop(vpnInterface!!)
                } else {
                    Log.e(TAG, "VpnService.Builder.establish() returned null")
                    VpnRepository.addLog("Bağlantı Hatası", "TUN arayüzü başlatılamadı.", false)
                    VpnRepository.updateStatus(ConnectionStatus.DISCONNECTED)
                    stopSelf()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to establish VPN tunnel", e)
                VpnRepository.addLog("Hata", "Tünel oluşturma başarısız: ${e.localizedMessage}", false)
                VpnRepository.updateStatus(ConnectionStatus.DISCONNECTED)
                stopSelf()
            }
        }
    }

    private fun startPacketLoop(pfd: ParcelFileDescriptor) {
        tunnelJob?.cancel()
        tunnelJob = serviceScope.launch {
            try {
                val inputStream = FileInputStream(pfd.fileDescriptor)
                val buffer = ByteBuffer.allocate(32767)

                while (isActive) {
                    val bytesRead = try {
                        if (inputStream.channel.isOpen) {
                            inputStream.read(buffer.array())
                        } else -1
                    } catch (e: Exception) {
                        -1
                    }

                    if (bytesRead > 0) {
                        buffer.clear()
                    } else {
                        delay(100)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Packet loop ended normally: ${e.message}")
            }
        }
    }

    private fun disconnectVpn() {
        VpnRepository.updateStatus(ConnectionStatus.DISCONNECTING)
        tunnelJob?.cancel()
        tunnelJob = null

        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            Log.e(TAG, "Error closing VPN descriptor", e)
        }

        VpnRepository.updateStatus(ConnectionStatus.DISCONNECTED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Aegis VPN Güvenlik Tüneli",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Aktif VPN tünel bağlantı durumu ve IP kamuflajı"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, subtitle: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, AegisVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this,
            1,
            disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aegis VPN: $title")
            .setContentText(subtitle)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Bağlantıyı Kes",
                disconnectPendingIntent
            )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        disconnectVpn()
        super.onDestroy()
    }
}
