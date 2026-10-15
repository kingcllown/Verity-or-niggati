package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vpn.VpnRepository
import com.example.vpn.model.ConnectionStatus
import com.example.vpn.model.NetworkAccelerationMode
import com.example.vpn.model.TunnelMode
import com.example.vpn.model.VpnProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aegis VPN", appName)
  }

  @Test
  fun `verify vpn repository server catalog loaded`() {
    val servers = VpnRepository.servers.value
    assertTrue("Servers should not be empty", servers.isNotEmpty())
    val selected = VpnRepository.selectedServer.value
    assertNotNull(selected)
  }

  @Test
  fun `verify protocol selection and security settings`() {
    VpnRepository.updateSettings { 
      it.copy(
        protocol = VpnProtocol.WIREGUARD, 
        killSwitchEnabled = true,
        totalIpCloakEnabled = true,
        ipv6LeakProtection = true,
        webRtcLeakProtection = true,
        strictGameShield = true,
        lz4CompressionEnabled = true,
        accelerationMode = NetworkAccelerationMode.TURBO_BBR,
        tunnelMode = TunnelMode.FULL_TUNNEL
      ) 
    }
    assertEquals(VpnProtocol.WIREGUARD, VpnRepository.settings.value.protocol)
    assertTrue(VpnRepository.settings.value.killSwitchEnabled)
    assertTrue(VpnRepository.settings.value.totalIpCloakEnabled)
    assertTrue(VpnRepository.settings.value.ipv6LeakProtection)
    assertTrue(VpnRepository.settings.value.webRtcLeakProtection)
    assertTrue(VpnRepository.settings.value.strictGameShield)
    assertTrue(VpnRepository.settings.value.lz4CompressionEnabled)
    assertEquals(NetworkAccelerationMode.TURBO_BBR, VpnRepository.settings.value.accelerationMode)
    assertEquals(TunnelMode.FULL_TUNNEL, VpnRepository.settings.value.tunnelMode)
  }

  @Test
  fun `verify split tunneling toggle`() {
    val targetPkg = "com.android.chrome"
    val initial = VpnRepository.splitApps.value.find { it.packageName == targetPkg }?.isBypassed ?: false
    VpnRepository.toggleAppBypass(targetPkg)
    val updated = VpnRepository.splitApps.value.find { it.packageName == targetPkg }?.isBypassed ?: false
    assertEquals(!initial, updated)
  }

  @Test
  fun `verify ip leak audit state`() {
    val audit = VpnRepository.auditResult.value
    assertNotNull(audit)
    assertTrue(audit.ipv6Protected)
    assertTrue(audit.webRtcBlocked)
  }

  @Test
  fun `verify vulnerability report structure`() {
    val report = VpnRepository.vulnerabilityReport.value
    assertNotNull(report)
    assertTrue("Vulnerability report should contain checks", report.items.isNotEmpty())
  }
}
