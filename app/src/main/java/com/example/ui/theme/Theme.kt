package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBgDeep,
    primaryContainer = CyberSurfaceElevated,
    onPrimaryContainer = NeonCyanLight,
    secondary = CyberBlue,
    onSecondary = CyberBgDeep,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = CyberBlue,
    tertiary = NeonEmerald,
    onTertiary = CyberBgDeep,
    background = CyberBg,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = InsecureRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
