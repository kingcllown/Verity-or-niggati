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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.InsecureRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.vpn.model.NetworkAccelerationMode
import com.example.vpn.model.SplitTunnelApp
import com.example.vpn.model.SystemVulnerabilityReport
import com.example.vpn.model.TunnelMode
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnSettings
import com.example.vpn.model.VulnerabilityStatus

@Composable
fun SecuritySettingsScreen(
    settings: VpnSettings,
    splitApps: List<SplitTunnelApp>,
    vulnerabilityReport: SystemVulnerabilityReport,
    onRunVulnerabilityScan: () -> Unit,
    onToggleAppBypass: (String) -> Unit,
    onUpdateSettings: ((VpnSettings) -> VpnSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Screen Header
        Column {
            Text(
                text = "Güvenlik & Açık Tarayıcı",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Askeri düzey güvenlik açığı taraması ve BBR hızlandırma merkezi",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Live In-Depth Vulnerability & Leak Scanner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CyberSurface)
                .border(
                    1.5.dp,
                    if (vulnerabilityReport.securityScore == 100) NeonEmerald.copy(alpha = 0.8f) else WarningAmber.copy(alpha = 0.8f),
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (vulnerabilityReport.securityScore == 100) NeonEmerald.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = if (vulnerabilityReport.securityScore == 100) NeonEmerald else WarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GÜVENLİK PUANI: ${vulnerabilityReport.securityScore}/100",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = if (vulnerabilityReport.securityScore == 100) NeonEmerald else WarningAmber
                                )
                            }
                            Text(
                                text = if (vulnerabilityReport.vulnerabilityCount == 0) "0 Güvenlik Açığı • Sistem Askeri Standartta" else "${vulnerabilityReport.vulnerabilityCount} Risk Tespit Edildi (VPN'i Açın)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onRunVulnerabilityScan,
                        enabled = !vulnerabilityReport.isScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = CyberBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        if (vulnerabilityReport.isScanning) {
                            CircularProgressIndicator(
                                color = CyberBg,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (vulnerabilityReport.isScanning) "Taranıyor" else "Şimdi Tara",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Diagnostic check items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    vulnerabilityReport.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (item.status == VulnerabilityStatus.SECURE) Icons.Default.CheckCircle else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (item.status == VulnerabilityStatus.SECURE) NeonEmerald else InsecureRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = TextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (item.status == VulnerabilityStatus.SECURE) NeonEmerald.copy(alpha = 0.2f) else InsecureRed.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (item.status == VulnerabilityStatus.SECURE) "GÜVENLİ" else "AÇIKTA",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.status == VulnerabilityStatus.SECURE) NeonEmerald else InsecureRed
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Network Acceleration Modes
        Text(
            text = "AĞ HIZLANDIRMA & OYUN MODLARI (TURBO)",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = NeonCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NetworkAccelerationMode.values().forEach { mode ->
                val isSelected = settings.accelerationMode == mode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
                        .border(1.dp, if (isSelected) NeonCyan else CardBorder, RoundedCornerShape(14.dp))
                        .clickable { onUpdateSettings { it.copy(accelerationMode = mode) } }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                    ),
                                    color = if (isSelected) NeonCyan else TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonEmerald.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = mode.pingReduction,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonEmerald
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mode.description,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Seçili",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Mutlak IP & Oyun Sızıntı Kalkanları
        Text(
            text = "MUTLAK IP GİZLEME & OYUN KALKANLARI",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = NeonCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        // WebRTC & STUN Sızıntı Kalkanı
        SettingToggleCard(
            title = "WebRTC & STUN Sızıntı Kalkanı",
            subtitle = "Tarayıcıların (Chrome vb.) ve Discord/oyun ses motorlarının arka planda gerçek IP'nizi sorgulamasını tamamen bloke eder.",
            icon = Icons.Default.Language,
            isChecked = settings.webRtcLeakProtection,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(webRtcLeakProtection = isChecked) }
            },
            accentColor = NeonCyan,
            testTag = "webrtc_leak_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // IPv6 Sızıntı Engelleme
        SettingToggleCard(
            title = "IPv6 Sızıntı Engelleme (::/0 Kilitli)",
            subtitle = "İnternet servis sağlayıcınızın IPv6 üzerinden gerçek adresinizi web sitelerine vermesini %100 kilitler.",
            icon = Icons.Default.Fingerprint,
            isChecked = settings.ipv6LeakProtection,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(ipv6LeakProtection = isChecked) }
            },
            accentColor = NeonEmerald,
            testTag = "ipv6_leak_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Büyük Oyunlar Kamuflaj Kalkanı
        SettingToggleCard(
            title = "Büyük Oyunlar Kamuflaj Kalkanı",
            subtitle = "Steam, Riot Games (Valorant/LoL), Epic Games, PUBG, Roblox ve CoD sunucularında gerçek IP'yi tamamen gizler.",
            icon = Icons.Default.SportsEsports,
            isChecked = settings.strictGameShield,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(strictGameShield = isChecked) }
            },
            accentColor = CyberBlue,
            testTag = "game_shield_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Kill Switch (Ağ Kesici)
        SettingToggleCard(
            title = "Kill Switch (Ağ Kesici)",
            subtitle = "VPN bağlantısı 1 milisaniye bile kesilirse tüm interneti anında bloke eder; gerçek IP'nizin sızmasını sıfıra indirir.",
            icon = Icons.Default.VpnLock,
            isChecked = settings.killSwitchEnabled,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(killSwitchEnabled = isChecked) }
            },
            accentColor = InsecureRed,
            testTag = "kill_switch_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ekstra: LZ4 Veri Sıkıştırma
        SettingToggleCard(
            title = "LZ4 Paket Sıkıştırma (Mobil Veri Tasarrufu)",
            subtitle = "Şifreli ağ paketlerini donanım hızlandırmalı sıkıştırarak mobil veri kullanımını %16-25 azaltır ve veri hızını artırır.",
            icon = Icons.Default.Compress,
            isChecked = settings.lz4CompressionEnabled,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(lz4CompressionEnabled = isChecked) }
            },
            accentColor = NeonCyan,
            testTag = "lz4_compression_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ekstra: DPI & Sansür Karıştırıcı
        SettingToggleCard(
            title = "DPI & Sansür Karıştırıcı (TLS 1.3 Mimic)",
            subtitle = "VPN trafiğini sıradan güvenli HTTPS web trafiği gibi maskeleyerek ISS ve güvenlik duvarı engellerini atlatır.",
            icon = Icons.Default.Shield,
            isChecked = settings.dpiCamouflageEnabled,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(dpiCamouflageEnabled = isChecked) }
            },
            accentColor = CyberBlue,
            testTag = "dpi_camouflage_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Ekstra: Kuantum Anahtar Rotasyonu (PFS)
        SettingToggleCard(
            title = "Kuantum Anahtar Rotasyonu (PFS)",
            subtitle = "Mükemmel İleri Gizlilik (Perfect Forward Secrecy) için her 15 dakikada bir şifreleme anahtarlarını otomatik yeniler.",
            icon = Icons.Default.Key,
            isChecked = settings.quantumKeyRotationEnabled,
            onCheckedChange = { isChecked ->
                onUpdateSettings { it.copy(quantumKeyRotationEnabled = isChecked) }
            },
            accentColor = NeonEmerald,
            testTag = "quantum_rotation_toggle"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Split Tunneling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BÖLÜNMÜŞ TÜNEL (SPLIT TUNNELING)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = NeonCyan
            )

            Switch(
                checked = settings.splitTunnelingEnabled,
                onCheckedChange = { isChecked ->
                    onUpdateSettings { it.copy(splitTunnelingEnabled = isChecked) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBg,
                    checkedTrackColor = NeonCyan
                ),
                modifier = Modifier.testTag("split_tunnel_master_switch")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Seçtiğiniz uygulamalar doğrudan yerel internetinizden çıkar, VPN tünelini baypas eder (Bankacılık ve yerel servisler için idealdir).",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (settings.splitTunnelingEnabled) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberSurface)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                splitApps.forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleAppBypass(app.packageName) }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = app.isBypassed,
                                onCheckedChange = { onToggleAppBypass(app.packageName) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = NeonCyan,
                                    uncheckedColor = CardBorder,
                                    checkmarkColor = CyberBg
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = app.appName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${app.category} • ${if (app.isBypassed) "VPN'i Baypas Eder" else "VPN Korumasında"}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = if (app.isBypassed) CyberBlue else TextMuted
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: Protocols
        Text(
            text = "ŞİFRELEME PROTOKOLÜ SEÇİMİ",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = NeonCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            VpnProtocol.values().forEach { protocol ->
                ProtocolSelectionCard(
                    protocol = protocol,
                    isSelected = settings.protocol == protocol,
                    onSelect = {
                        onUpdateSettings { it.copy(protocol = protocol) }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: MTU Tuning Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = CyberBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Maksimum İletim Boyutu (MTU)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "${settings.mtu} bayt",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Oyunlarda paket gecikmesini azaltmak ve paket sızıntısını önlemek için MTU boyutu 1420'dir.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )

                Slider(
                    value = settings.mtu.toFloat(),
                    onValueChange = { newMtu ->
                        onUpdateSettings { it.copy(mtu = newMtu.toInt()) }
                    },
                    valueRange = 1280f..1500f,
                    steps = 21,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = CardBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingToggleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: androidx.compose.ui.graphics.Color,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBg,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

@Composable
private fun ProtocolSelectionCard(
    protocol: VpnProtocol,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
            .border(
                1.dp,
                if (isSelected) NeonCyan else CardBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = protocol.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = if (isSelected) NeonCyan else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = protocol.tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonCyan else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = protocol.cipher,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = CyberBlue
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = protocol.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seçili",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
