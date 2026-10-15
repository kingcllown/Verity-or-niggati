package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ActiveServerDashboardCard
import com.example.ui.components.ConnectOrb
import com.example.ui.components.CyberWaveVisualizer
import com.example.ui.components.TelemetryCards
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.InsecureRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.vpn.model.ConnectionStatus
import com.example.vpn.model.IpLeakAuditResult
import com.example.vpn.model.ServerLocation
import com.example.vpn.model.VpnSettings
import com.example.vpn.model.VpnStats

@Composable
fun DashboardScreen(
    status: ConnectionStatus,
    selectedServer: ServerLocation,
    settings: VpnSettings,
    stats: VpnStats,
    auditResult: IpLeakAuditResult,
    onConnectToggle: () -> Unit,
    onNavigateServers: () -> Unit,
    onNavigateSettings: () -> Unit,
    onRunAudit: () -> Unit,
    onTurboBoost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = status == ConnectionStatus.CONNECTED

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Bar & Security Score Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Aegis Shield",
                        tint = if (isConnected) NeonEmerald else NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "AEGIS VPN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "MUTLAK IP GİZLEME & ŞİFRELEME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp
                        ),
                        color = NeonCyan
                    )
                }
            }

            // Security Score Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isConnected) NeonEmerald.copy(alpha = 0.15f) else InsecureRed.copy(alpha = 0.15f)
                    )
                    .border(
                        1.dp,
                        if (isConnected) NeonEmerald.copy(alpha = 0.5f) else InsecureRed.copy(alpha = 0.5f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isConnected) NeonEmerald else InsecureRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isConnected) "IP GİZLİ • %100" else "KORUMASIZ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isConnected) NeonEmerald else InsecureRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Animated Cyber Connect Orb
        ConnectOrb(
            status = status,
            onToggle = onConnectToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Label & Protocol Pill
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (status) {
                    ConnectionStatus.CONNECTED -> "GERÇEK IP KORUNDU • SAHTE IP VERİLİYOR"
                    ConnectionStatus.CONNECTING -> "IP KAMUFLAJ TÜNELİ OLUŞTURULUYOR..."
                    ConnectionStatus.DISCONNECTING -> "BAĞLANTI SONLANDIRILIYOR..."
                    ConnectionStatus.DISCONNECTED -> "GERÇEK IP AÇIKTA • KORUMA PASİF"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = when (status) {
                    ConnectionStatus.CONNECTED -> NeonEmerald
                    ConnectionStatus.CONNECTING -> NeonCyan
                    ConnectionStatus.DISCONNECTING -> WarningAmber
                    ConnectionStatus.DISCONNECTED -> TextSecondary
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Protocol & Cipher Pill (Clickable to jump to settings)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onNavigateSettings)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${settings.protocol.displayName} • ${settings.accelerationMode.title}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Real-Time Oscillating Wave Visualizer
        CyberWaveVisualizer(
            isConnected = isConnected,
            downloadSpeedMbps = stats.downloadSpeedMbps,
            uploadSpeedMbps = stats.uploadSpeedMbps
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Server Card
        ActiveServerDashboardCard(
            server = selectedServer,
            onServerClick = onNavigateServers,
            modifier = Modifier.testTag("active_server_card")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Telemetry Metrics & Live IP Cloaking & Turbo Boost
        TelemetryCards(
            stats = stats,
            auditResult = auditResult,
            isConnected = isConnected,
            onRunAudit = onRunAudit,
            onTurboBoost = onTurboBoost
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
