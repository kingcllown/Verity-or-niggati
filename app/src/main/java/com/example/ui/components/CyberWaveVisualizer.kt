package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

@Composable
fun CyberWaveVisualizer(
    isConnected: Boolean,
    downloadSpeedMbps: Float,
    uploadSpeedMbps: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_oscillation")

    val phaseOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_1"
    )

    val phaseOffset2 by infiniteTransition.animateFloat(
        initialValue = 6.28318f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_2"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = if (isConnected) NeonEmerald else TextMuted,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "CANLI ŞİFRELİ TRAFİK DALGASI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isConnected) "ŞİFRELİ AKIŞ AKTİF" else "BEKLEMEDE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isConnected) NeonEmerald else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Wave
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val width = size.width
                val height = size.height
                val midY = height / 2f

                if (!isConnected) {
                    // Flat idle baseline with subtle pulse
                    drawLine(
                        color = CardBorder,
                        start = Offset(0f, midY),
                        end = Offset(width, midY),
                        strokeWidth = 2.dp.toPx()
                    )
                    return@Canvas
                }

                val amplitude1 = (height * 0.32f)
                val amplitude2 = (height * 0.22f)

                val pathWave1 = Path()
                val pathFill1 = Path()
                val pathWave2 = Path()

                pathFill1.moveTo(0f, height)
                pathWave1.moveTo(0f, midY)
                pathWave2.moveTo(0f, midY)

                val points = 60
                val dx = width / points

                for (i in 0..points) {
                    val x = i * dx
                    val angle1 = (i / points.toFloat() * 4 * Math.PI) + phaseOffset1
                    val y1 = midY + (amplitude1 * sin(angle1)).toFloat()

                    val angle2 = (i / points.toFloat() * 3 * Math.PI) + phaseOffset2
                    val y2 = midY + (amplitude2 * sin(angle2)).toFloat()

                    if (i == 0) {
                        pathWave1.moveTo(x, y1)
                        pathFill1.lineTo(x, y1)
                        pathWave2.moveTo(x, y2)
                    } else {
                        pathWave1.lineTo(x, y1)
                        pathFill1.lineTo(x, y1)
                        pathWave2.lineTo(x, y2)
                    }
                }

                pathFill1.lineTo(width, height)
                pathFill1.close()

                // Draw filled gradient under wave
                drawPath(
                    path = pathFill1,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )

                // Draw Primary Wave (Download - Cyan)
                drawPath(
                    path = pathWave1,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            CyberBlue,
                            NeonCyan,
                            NeonEmerald
                        )
                    ),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Secondary Wave (Upload - Emerald)
                drawPath(
                    path = pathWave2,
                    color = NeonEmerald.copy(alpha = 0.65f),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}
