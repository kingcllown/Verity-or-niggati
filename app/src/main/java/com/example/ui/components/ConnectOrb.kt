package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBgDeep
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.InsecureRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarningAmber
import com.example.vpn.model.ConnectionStatus
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConnectOrb(
    status: ConnectionStatus,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth physics-based press scale
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_spring"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "cyber_animations")

    // Clockwise radar rotation
    val clockRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (status == ConnectionStatus.CONNECTING) 1400 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clock_rotation"
    )

    // Counter-clockwise secondary ring rotation
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    // Sonar pulse wave 1
    val sonarPulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (status == ConnectionStatus.CONNECTED) 2400 else 1400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar_pulse_1"
    )

    val sonarAlpha1 by infiniteTransition.animateFloat(
        initialValue = if (status == ConnectionStatus.CONNECTED) 0.45f else if (status == ConnectionStatus.CONNECTING) 0.6f else 0.15f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (status == ConnectionStatus.CONNECTED) 2400 else 1400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar_alpha_1"
    )

    // Breathing inner core glow
    val coreGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_glow_alpha"
    )

    // Color transition based on status
    val glowColor by animateColorAsState(
        targetValue = when (status) {
            ConnectionStatus.CONNECTED -> NeonEmerald
            ConnectionStatus.CONNECTING -> NeonCyan
            ConnectionStatus.DISCONNECTING -> WarningAmber
            ConnectionStatus.DISCONNECTED -> InsecureRed
        },
        animationSpec = tween(500),
        label = "status_color_anim"
    )

    Box(
        modifier = modifier
            .size(260.dp)
            .scale(pressScale)
            .testTag("vpn_connect_button"),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Expanding Sonar Radar Wave
        Canvas(modifier = Modifier.size(260.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = 80.dp.toPx()

            drawCircle(
                color = glowColor.copy(alpha = sonarAlpha1),
                radius = baseRadius * sonarPulse1,
                center = center,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        // Layer 2: Cyber Rings & Particle Trackers
        Canvas(modifier = Modifier.size(240.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = (size.minDimension / 2f) - 6.dp.toPx()
            val middleRadius = outerRadius - 14.dp.toPx()

            // Outer Dashed Technical Ring
            drawCircle(
                color = glowColor.copy(alpha = 0.25f),
                radius = outerRadius,
                center = center,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )

            // Middle Counter-rotating Ring with tick marks
            drawCircle(
                color = glowColor.copy(alpha = 0.18f),
                radius = middleRadius,
                center = center,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 10f, 10f, 10f), counterRotation)
                )
            )

            // Dynamic Sweeping Laser Radar Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Transparent,
                        glowColor.copy(alpha = 0.15f),
                        glowColor.copy(alpha = 0.85f),
                        Color.White
                    )
                ),
                startAngle = clockRotation,
                sweepAngle = if (status == ConnectionStatus.CONNECTING) 220f else 110f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Orbiting Quantum Particle Nodes (When connected)
            if (status == ConnectionStatus.CONNECTED || status == ConnectionStatus.CONNECTING) {
                for (i in 0 until 4) {
                    val angleRad = Math.toRadians((clockRotation + (i * 90.0)))
                    val particleX = center.x + (outerRadius * cos(angleRad)).toFloat()
                    val particleY = center.y + (outerRadius * sin(angleRad)).toFloat()

                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(particleX, particleY)
                    )
                    drawCircle(
                        color = glowColor.copy(alpha = 0.5f),
                        radius = 6.dp.toPx(),
                        center = Offset(particleX, particleY)
                    )
                }
            }
        }

        // Layer 3: Glowing Core Tactile Orb
        Box(
            modifier = Modifier
                .size(170.dp)
                .shadow(
                    elevation = if (status == ConnectionStatus.CONNECTED) 32.dp else 12.dp,
                    shape = CircleShape,
                    spotColor = glowColor,
                    ambientColor = glowColor
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CyberSurface,
                            CyberBgDeep
                        )
                    )
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggle
                ),
            contentAlignment = Alignment.Center
        ) {
            // Ambient inner glow ring
            Canvas(modifier = Modifier.size(170.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = coreGlowAlpha * 0.4f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2f
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (status) {
                    ConnectionStatus.CONNECTED -> {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Güvenli Bağlantı",
                            tint = NeonEmerald,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    ConnectionStatus.CONNECTING -> {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Bağlanıyor",
                            tint = NeonCyan,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    ConnectionStatus.DISCONNECTING -> {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Bağlantı Kesiliyor",
                            tint = WarningAmber,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    ConnectionStatus.DISCONNECTED -> {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Bağlan",
                            tint = InsecureRed,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (status) {
                        ConnectionStatus.CONNECTED -> "KORUNUYOR"
                        ConnectionStatus.CONNECTING -> "BAĞLANIYOR"
                        ConnectionStatus.DISCONNECTING -> "DURDURULUYOR"
                        ConnectionStatus.DISCONNECTED -> "BAĞLAN"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.5.sp
                    ),
                    color = glowColor
                )

                Text(
                    text = when (status) {
                        ConnectionStatus.CONNECTED -> "AES-256 / 10 Gbps"
                        ConnectionStatus.CONNECTING -> "EL SIKIŞMASI..."
                        ConnectionStatus.DISCONNECTING -> "KAPANIYOR..."
                        ConnectionStatus.DISCONNECTED -> "DOKUN"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextMuted
                )
            }
        }
    }
}
