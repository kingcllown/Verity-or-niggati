package com.example.vpn.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class TunnelMode(
    val title: String,
    val description: String
) {
    FULL_TUNNEL(
        title = "Tam Tünel (0.0.0.0/0 & ::/0)",
        description = "Tüm cihaz ağ trafiğini zorunlu olarak şifreli VPN tünelinden geçirir; web ve oyunlarda gerçek IP'yi %100 gizler."
    ),
    DNS_THREAT_SHIELD(
        title = "CyberShield Akıllı Kalkan",
        description = "DNS ve WebRTC sızıntılarını önler, reklam ve zararlı yazılımları süzer, kesintisiz maksimum hız sunar."
    )
}

enum class NetworkAccelerationMode(
    val title: String,
    val description: String,
    val pingReduction: String,
    val tag: String
) {
    TURBO_BBR(
        title = "BBR v3 Turbo Hızlandırıcı",
        description = "Google BBR tıkanıklık kontrol algoritması ile hat darboğazlarını aşar, veri hızını %40 artırır.",
        pingReduction = "-15 ms",
        tag = "TURBO"
    ),
    ULTRA_GAMING(
        title = "Oyun Ping & Jitter Sabitleyici",
        description = "UDP oyun paketlerini (Valorant, CS2, LoL, PUBG, Roblox) en öncelikli sıraya alır, gecikmeyi minimuma indirir.",
        pingReduction = "-22 ms",
        tag = "OYUN"
    ),
    STREAMING_4K(
        title = "4K / 8K Medya & Yayın Hızlandırıcı",
        description = "Genişletilmiş TCP pencere boyutu ile YouTube, Netflix ve Twitch yayınlarını anında tamponlar.",
        pingReduction = "10 Gbps",
        tag = "YAYIN"
    ),
    BALANCED(
        title = "Standart Optimum Denge",
        description = "Günlük kullanım için enerji verimli dengeli şifreleme ve kararlı bağlantı.",
        pingReduction = "Optimum",
        tag = "DENGE"
    )
}

enum class VpnProtocol(
    val displayName: String,
    val cipher: String,
    val description: String,
    val tag: String
) {
    WIREGUARD(
        displayName = "WireGuard Ultra",
        cipher = "ChaCha20-Poly1305 (Noise IK)",
        description = "Yeni nesil ultra hızlı çekirdek protokolü. Minimum gecikme ve pil tasarrufu.",
        tag = "EN HIZLI"
    ),
    OPENVPN_UDP(
        displayName = "OpenVPN Military",
        cipher = "AES-256-GCM / SHA-512",
        description = "Askeri düzey şifreleme ve maksimum güvenlik standardı.",
        tag = "ASKERİ DÜZEY"
    ),
    STEALTH_SHADOW(
        displayName = "Stealth Obfuscation",
        cipher = "Shadow-TLS 1.3 + ChaCha",
        description = "DPI (Derin Paket İnceleme) atlatma ve sansür engelleme kalkanı.",
        tag = "GİZLİ TÜNEL"
    ),
    QUANTUM_SHIELD(
        displayName = "QuantumShield v2",
        cipher = "Post-Quantum Kyber-768 + AES-256",
        description = "Kuantum bilgisayar saldırılarına karşı geleceğe hazır hibrit koruma.",
        tag = "KUANTUM GÜVENLİK"
    )
}

enum class ServerCategory(val label: String) {
    ALL("Tümü"),
    FASTEST("En Hızlı"),
    GAMING("Düşük Ping (Oyun)"),
    STREAMING("4K Yayın"),
    PRIVACY("Ultra Gizlilik / P2P"),
    DOUBLE_HOP("Çift Tünel (Double-Hop)")
}

data class ServerLocation(
    val id: String,
    val country: String,
    val city: String,
    val flagEmoji: String,
    val pingMs: Int,
    val loadPercentage: Int,
    val bandwidthGbps: Int = 10,
    val ipAddress: String,
    val category: ServerCategory = ServerCategory.FASTEST,
    val isDoubleHop: Boolean = false,
    val hopVia: String? = null
)

data class SplitTunnelApp(
    val packageName: String,
    val appName: String,
    val category: String,
    val isBypassed: Boolean = false
)

data class VpnSettings(
    val killSwitchEnabled: Boolean = true,
    val totalIpCloakEnabled: Boolean = true,
    val ipv6LeakProtection: Boolean = true,
    val webRtcLeakProtection: Boolean = true,
    val strictGameShield: Boolean = true,
    val dnsLeakProtection: Boolean = true,
    val threatBlockerEnabled: Boolean = true,
    val lz4CompressionEnabled: Boolean = true,
    val fastRoamingEnabled: Boolean = true,
    val dpiCamouflageEnabled: Boolean = true,
    val quantumKeyRotationEnabled: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val bypassedApps: Set<String> = emptySet(),
    val tunnelMode: TunnelMode = TunnelMode.FULL_TUNNEL,
    val accelerationMode: NetworkAccelerationMode = NetworkAccelerationMode.TURBO_BBR,
    val protocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val mtu: Int = 1420,
    val autoConnectOnInsecureWifi: Boolean = true,
    val selectedDns: String = "Cloudflare (1.1.1.1) + Quad9 (9.9.9.9)"
)

data class VpnStats(
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val totalDownloadedMb: Float = 0f,
    val totalUploadedMb: Float = 0f,
    val durationSeconds: Long = 0L,
    val maskedVirtualIp: String = "185.220.101.42",
    val realIp: String = "TAMAMEN GİZLENDİ (%100 Koruma)",
    val packetsProtected: Long = 0L,
    val threatsBlockedCount: Int = 0,
    val dataSavedMb: Float = 0f,
    val currentPingReductionMs: Int = 18,
    val isRealNetworkOnline: Boolean = true,
    val isIpCloakActive: Boolean = true
)

data class IpLeakAuditResult(
    val isAuditing: Boolean = false,
    val visiblePublicIp: String = "185.220.101.42",
    val realIpExposed: Boolean = false,
    val ipv6Protected: Boolean = true,
    val webRtcBlocked: Boolean = true,
    val dnsEncrypted: Boolean = true,
    val gameServersCloaked: Boolean = true,
    val testedSites: List<String> = listOf("Google / Cloudflare", "Riot Games & Steam", "Discord & WebRTC", "Speedtest & Netflix"),
    val verdict: String = "GERÇEK IP %100 GİZLENDİ • SIZINTI YOK",
    val auditTimestamp: String = "Şimdi"
)

data class VulnerabilityCheckItem(
    val id: String,
    val title: String,
    val description: String,
    val status: VulnerabilityStatus,
    val riskLevel: String
)

enum class VulnerabilityStatus {
    SECURE,
    WARNING,
    FIXING
}

data class SystemVulnerabilityReport(
    val isScanning: Boolean = false,
    val securityScore: Int = 100,
    val vulnerabilityCount: Int = 0,
    val lastScanTime: String = "Şimdi",
    val items: List<VulnerabilityCheckItem> = emptyList()
)

data class SecurityLogItem(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String,
    val title: String,
    val detail: String,
    val isSecure: Boolean = true
)
