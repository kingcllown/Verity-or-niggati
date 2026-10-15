package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.vpn.AegisVpnService
import com.example.vpn.VpnRepository
import com.example.vpn.model.ConnectionStatus
import com.example.vpn.model.IpLeakAuditResult
import com.example.vpn.model.SecurityLogItem
import com.example.vpn.model.ServerLocation
import com.example.vpn.model.SplitTunnelApp
import com.example.vpn.model.SystemVulnerabilityReport
import com.example.vpn.model.VpnSettings
import com.example.vpn.model.VpnStats
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val connectionStatus: StateFlow<ConnectionStatus> = VpnRepository.connectionStatus
    val selectedServer: StateFlow<ServerLocation> = VpnRepository.selectedServer
    val servers: StateFlow<List<ServerLocation>> = VpnRepository.servers
    val settings: StateFlow<VpnSettings> = VpnRepository.settings
    val stats: StateFlow<VpnStats> = VpnRepository.stats
    val splitApps: StateFlow<List<SplitTunnelApp>> = VpnRepository.splitApps
    val logs: StateFlow<List<SecurityLogItem>> = VpnRepository.logs
    val auditResult: StateFlow<IpLeakAuditResult> = VpnRepository.auditResult
    val vulnerabilityReport: StateFlow<SystemVulnerabilityReport> = VpnRepository.vulnerabilityReport

    fun toggleConnection(context: Context) {
        val current = connectionStatus.value
        if (current == ConnectionStatus.CONNECTED || current == ConnectionStatus.CONNECTING) {
            AegisVpnService.stopVpn(context)
        } else {
            AegisVpnService.startVpn(context)
        }
    }

    fun selectServer(server: ServerLocation) {
        VpnRepository.selectServer(server)
    }

    fun selectOptimalServer() {
        VpnRepository.selectOptimalServer()
    }

    fun refreshPings() {
        VpnRepository.refreshPings()
    }

    fun runIpLeakAudit() {
        VpnRepository.runIpLeakAudit()
    }

    fun runVulnerabilityScan() {
        VpnRepository.runVulnerabilityScan()
    }

    fun applyTurboAcceleration() {
        VpnRepository.applyTurboAcceleration()
    }

    fun toggleAppBypass(packageName: String) {
        VpnRepository.toggleAppBypass(packageName)
    }

    fun updateSettings(transform: (VpnSettings) -> VpnSettings) {
        VpnRepository.updateSettings(transform)
    }
}
