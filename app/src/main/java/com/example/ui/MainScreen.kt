package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.screens.AuditLogScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SecuritySettingsScreen
import com.example.ui.screens.ServerListScreen
import com.example.ui.screens.SpeedTestScreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.vpn.model.ConnectionStatus

enum class AppNavTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    DASHBOARD("Kalkan", Icons.Default.Security, "tab_dashboard"),
    SERVERS("Sunucular", Icons.Default.Public, "tab_servers"),
    SPEED_TEST("Hız Radarı", Icons.Default.Speed, "tab_speed_test"),
    SECURITY("Güvenlik", Icons.Default.Dns, "tab_security"),
    LOGS("Günlük", Icons.Default.ListAlt, "tab_logs")
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var currentTab by remember { mutableStateOf(AppNavTab.DASHBOARD) }

    val status by viewModel.connectionStatus.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val splitApps by viewModel.splitApps.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val auditResult by viewModel.auditResult.collectAsState()
    val vulnerabilityReport by viewModel.vulnerabilityReport.collectAsState()

    // Notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Graceful handling */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Android VpnService.prepare() permission launcher
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.toggleConnection(context)
        }
    }

    val onConnectAction: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        if (status == ConnectionStatus.CONNECTED || status == ConnectionStatus.CONNECTING) {
            viewModel.toggleConnection(context)
        } else if (status == ConnectionStatus.DISCONNECTED) {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                vpnPrepareLauncher.launch(prepareIntent)
            } else {
                viewModel.toggleConnection(context)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = CyberBg,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav"),
                containerColor = CyberSurface,
                tonalElevation = 8.dp
            ) {
                AppNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) CyberBg else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) NeonCyan else TextMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonCyan,
                            selectedIconColor = CyberBg,
                            unselectedIconColor = TextMuted,
                            selectedTextColor = NeonCyan,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> (width * 0.12f).toInt() } + fadeIn(animationSpec = tween(240))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(280)) { width -> -(width * 0.12f).toInt() } + fadeOut(animationSpec = tween(240)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> -(width * 0.12f).toInt() } + fadeIn(animationSpec = tween(240))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(280)) { width -> (width * 0.12f).toInt() } + fadeOut(animationSpec = tween(240)))
                    }
                },
                label = "screen_navigation"
            ) { targetTab ->
                when (targetTab) {
                    AppNavTab.DASHBOARD -> {
                        DashboardScreen(
                            status = status,
                            selectedServer = selectedServer,
                            settings = settings,
                            stats = stats,
                            auditResult = auditResult,
                            onConnectToggle = onConnectAction,
                            onNavigateServers = { currentTab = AppNavTab.SERVERS },
                            onNavigateSettings = { currentTab = AppNavTab.SECURITY },
                            onRunAudit = { viewModel.runIpLeakAudit() },
                            onTurboBoost = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.applyTurboAcceleration()
                            }
                        )
                    }
                    AppNavTab.SERVERS -> {
                        ServerListScreen(
                            servers = servers,
                            selectedServer = selectedServer,
                            onSelectServer = { server ->
                                viewModel.selectServer(server)
                            },
                            onSmartConnect = {
                                viewModel.selectOptimalServer()
                                currentTab = AppNavTab.DASHBOARD
                            },
                            onRefreshPings = {
                                viewModel.refreshPings()
                            },
                            onBack = {
                                currentTab = AppNavTab.DASHBOARD
                            }
                        )
                    }
                    AppNavTab.SPEED_TEST -> {
                        SpeedTestScreen(
                            currentServer = selectedServer,
                            isConnected = status == ConnectionStatus.CONNECTED
                        )
                    }
                    AppNavTab.SECURITY -> {
                        SecuritySettingsScreen(
                            settings = settings,
                            splitApps = splitApps,
                            vulnerabilityReport = vulnerabilityReport,
                            onRunVulnerabilityScan = {
                                viewModel.runVulnerabilityScan()
                            },
                            onToggleAppBypass = { pkg ->
                                viewModel.toggleAppBypass(pkg)
                            },
                            onUpdateSettings = { transform ->
                                viewModel.updateSettings(transform)
                            }
                        )
                    }
                    AppNavTab.LOGS -> {
                        AuditLogScreen(
                            logs = logs
                        )
                    }
                }
            }
        }
    }
}
