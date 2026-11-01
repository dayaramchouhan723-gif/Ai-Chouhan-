package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WireframeStar(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    variant: String = "Crimson Wireframe Star",
    primaryColor: Color = Color(0xFFFF1E27),
    glowColor: Color = Color(0xFFFF4D4D),
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StarAnimation")
    val rotation by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "Rotation"
        )
    } else {
        rememberInfiniteTransition(label = "Static").animateFloat(
            initialValue = 0f, targetValue = 0f, animationSpec = infiniteRepeatable(tween(1000)), label = "Static"
        )
    }

    val pulse by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Pulse"
        )
    } else {
        rememberInfiniteTransition(label = "Static").animateFloat(
            initialValue = 1f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(1000)), label = "Static"
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2.2f * pulse

            // Background Glowing Radial Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.45f),
                        primaryColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.35f
                ),
                radius = baseRadius * 1.35f,
                center = center
            )

            rotate(degrees = rotation, pivot = center) {
                when (variant) {
                    "Cyber Ruby Star" -> drawCyberRubyStar(center, baseRadius, primaryColor, glowColor)
                    "Obsidian Blood Star" -> drawObsidianBloodStar(center, baseRadius, primaryColor, glowColor)
                    "Neon Crimson Pulsar" -> drawNeonCrimsonPulsar(center, baseRadius, primaryColor, glowColor)
                    else -> drawCrimsonWireframeStar(center, baseRadius, primaryColor, glowColor)
                }
            }
        }
    }
}

// 1. Crimson Wireframe Star (Default)
private fun DrawScope.drawCrimsonWireframeStar(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    glowColor: Color
) {
    val outerPoints = calculateStarPoints(center, radius, 5, 0f)
    val innerPoints = calculateStarPoints(center, radius * 0.42f, 5, (PI / 5).toFloat())

    // Connect outer vertices to form 5-point pentagram star
    val starPath = Path()
    for (i in 0 until 5) {
        val o = outerPoints[i]
        val inn = innerPoints[i]
        if (i == 0) starPath.moveTo(o.x, o.y) else starPath.lineTo(o.x, o.y)
        starPath.lineTo(inn.x, inn.y)
    }
    starPath.close()

    // Outer Glow Stroke
    drawPath(
        path = starPath,
        color = glowColor.copy(alpha = 0.4f),
        style = Stroke(width = 8f)
    )
    // Primary Wireframe Stroke
    drawPath(
        path = starPath,
        color = primaryColor,
        style = Stroke(width = 3.5f)
    )

    // Inner wireframe diagonals (Pentagram chords)
    for (i in 0 until 5) {
        val nextIndex = (i + 2) % 5
        drawLine(
            color = glowColor.copy(alpha = 0.6f),
            start = outerPoints[i],
            end = outerPoints[nextIndex],
            strokeWidth = 2f
        )
        // Center spokes
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = center,
            end = outerPoints[i],
            strokeWidth = 1.5f
        )
        // Node markers
        drawCircle(
            color = Color.White,
            radius = 3.5f,
            center = outerPoints[i]
        )
    }

    // Center Core Node
    drawCircle(color = glowColor, radius = 6f, center = center)
    drawCircle(color = Color.White, radius = 2.5f, center = center)
}

// 2. Cyber Ruby Star (Faceted Diamond Gem)
private fun DrawScope.drawCyberRubyStar(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    glowColor: Color
) {
    val outer = calculateStarPoints(center, radius, 5, 0f)
    val inner = calculateStarPoints(center, radius * 0.45f, 5, (PI / 5).toFloat())

    // Draw faceted triangles for gemstone look
    for (i in 0 until 5) {
        val nextInner = inner[(i + 4) % 5]
        val currInner = inner[i]

        val facet1 = Path().apply {
            moveTo(center.x, center.y)
            lineTo(outer[i].x, outer[i].y)
            lineTo(currInner.x, currInner.y)
            close()
        }
        val facet2 = Path().apply {
            moveTo(center.x, center.y)
            lineTo(outer[i].x, outer[i].y)
            lineTo(nextInner.x, nextInner.y)
            close()
        }

        drawPath(
            path = facet1,
            color = if (i % 2 == 0) primaryColor.copy(alpha = 0.65f) else glowColor.copy(alpha = 0.85f),
            style = Fill
        )
        drawPath(
            path = facet2,
            color = if (i % 2 == 0) glowColor.copy(alpha = 0.5f) else primaryColor.copy(alpha = 0.4f),
            style = Fill
        )
        drawPath(path = facet1, color = Color.White.copy(alpha = 0.9f), style = Stroke(width = 2f))
    }
}

// 3. Obsidian Blood Star (Dark armor plate with blood red neon edges)
private fun DrawScope.drawObsidianBloodStar(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    glowColor: Color
) {
    val outer = calculateStarPoints(center, radius, 5, 0f)
    val inner = calculateStarPoints(center, radius * 0.38f, 5, (PI / 5).toFloat())

    val starPath = Path()
    for (i in 0 until 5) {
        if (i == 0) starPath.moveTo(outer[i].x, outer[i].y) else starPath.lineTo(outer[i].x, outer[i].y)
        starPath.lineTo(inner[i].x, inner[i].y)
    }
    starPath.close()

    // Dark Obsidian Core Fill
    drawPath(path = starPath, color = Color(0xFF140508), style = Fill)
    // Blood Red Razor Border
    drawPath(path = starPath, color = primaryColor, style = Stroke(width = 4f))

    // Inner Concentric Star
    val innerStar = Path()
    val subOuter = calculateStarPoints(center, radius * 0.6f, 5, 0f)
    val subInner = calculateStarPoints(center, radius * 0.22f, 5, (PI / 5).toFloat())
    for (i in 0 until 5) {
        if (i == 0) innerStar.moveTo(subOuter[i].x, subOuter[i].y) else innerStar.lineTo(subOuter[i].x, subOuter[i].y)
        innerStar.lineTo(subInner[i].x, subInner[i].y)
    }
    innerStar.close()
    drawPath(path = innerStar, color = glowColor, style = Stroke(width = 2f))
}

// 4. Neon Crimson Pulsar (Dual quantum rings & radiant bursts)
private fun DrawScope.drawNeonCrimsonPulsar(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    glowColor: Color
) {
    // Concentric Quantum Orbital Rings
    drawCircle(
        color = glowColor.copy(alpha = 0.5f),
        radius = radius * 0.95f,
        center = center,
        style = Stroke(width = 2.5f)
    )
    drawCircle(
        color = primaryColor.copy(alpha = 0.4f),
        radius = radius * 0.65f,
        center = center,
        style = Stroke(width = 1.5f)
    )

    // 10-Point Radiant Pulsar Wireframe
    val points10 = calculateStarPoints(center, radius, 10, 0f)
    val inner10 = calculateStarPoints(center, radius * 0.3f, 10, (PI / 10).toFloat())

    val pulsarPath = Path()
    for (i in 0 until 10) {
        if (i == 0) pulsarPath.moveTo(points10[i].x, points10[i].y) else pulsarPath.lineTo(points10[i].x, points10[i].y)
        pulsarPath.lineTo(inner10[i].x, inner10[i].y)
    }
    pulsarPath.close()

    drawPath(path = pulsarPath, color = primaryColor, style = Stroke(width = 2.5f))
    drawPath(path = pulsarPath, color = Color.White.copy(alpha = 0.3f), style = Fill)
}

private fun calculateStarPoints(center: Offset, radius: Float, count: Int, startAngle: Float): List<Offset> {
    val points = mutableListOf<Offset>()
    val step = (2 * PI / count).toFloat()
    for (i in 0 until count) {
        val angle = -PI.toFloat() / 2f + startAngle + i * step
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        points.add(Offset(x, y))
    }
    return points
}
