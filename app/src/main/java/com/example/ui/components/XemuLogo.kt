package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuViolet

/**
 * Geometric Vector XEMU Emblem.
 * Draws the signature futuristic angular "X" with glowing dual neon bars and central nexus.
 */
@Composable
fun XemuEmblem(
    modifier: Modifier = Modifier,
    primaryColor: Color = XemuCyan,
    secondaryColor: Color = XemuViolet,
    glowAlpha: Float = 0.8f,
    glowWidthFactor: Float = 1.0f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.14f * glowWidthFactor

        val gradient1 = Brush.linearGradient(
            colors = listOf(primaryColor, secondaryColor),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
        val gradient2 = Brush.linearGradient(
            colors = listOf(secondaryColor, primaryColor),
            start = Offset(w, 0f),
            end = Offset(0f, h)
        )

        // Outer glow path
        val glowStroke = strokeWidth * 1.6f
        val glowBrush = Brush.radialGradient(
            colors = listOf(primaryColor.copy(alpha = 0.35f * glowAlpha), Color.Transparent),
            center = Offset(w / 2f, h / 2f),
            radius = w * 0.65f
        )
        drawCircle(brush = glowBrush, radius = w * 0.55f, center = Offset(w / 2f, h / 2f))

        // First diagonal stroke: Top-Left to Bottom-Right with stylized split
        val path1 = Path().apply {
            moveTo(w * 0.15f, h * 0.15f)
            lineTo(w * 0.42f, h * 0.42f)
            moveTo(w * 0.58f, h * 0.58f)
            lineTo(w * 0.85f, h * 0.85f)
        }
        drawPath(
            path = path1,
            brush = gradient1,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Second continuous diagonal: Top-Right to Bottom-Left
        drawLine(
            brush = gradient2,
            start = Offset(w * 0.85f, h * 0.15f),
            end = Offset(w * 0.15f, h * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Central diamond nexus node
        val diamond = Path().apply {
            moveTo(w * 0.5f, h * 0.38f)
            lineTo(w * 0.62f, h * 0.5f)
            lineTo(w * 0.5f, h * 0.62f)
            lineTo(w * 0.38f, h * 0.5f)
            close()
        }
        drawPath(path = diamond, color = primaryColor.copy(alpha = 0.9f * glowAlpha))
    }
}

/**
 * Header logo for top-bars and sidebars.
 */
@Composable
fun XemuHeaderLogo(
    modifier: Modifier = Modifier,
    emblemSize: Dp = 32.dp,
    showSubtitle: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        XemuEmblem(
            modifier = Modifier.size(emblemSize)
        )
        Column {
            Text(
                text = "XEMU",
                color = XemuTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            if (showSubtitle) {
                Text(
                    text = "PC HUB",
                    color = XemuCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

/**
 * Animated Loading Logo with rotating orbital neon arcs.
 */
@Composable
fun XemuLoadingLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 64.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "xemu_loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.width * 0.05f
            drawArc(
                brush = Brush.sweepGradient(listOf(Color.Transparent, XemuCyan, XemuViolet, Color.Transparent)),
                startAngle = rotation,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        XemuEmblem(
            modifier = Modifier.size(sizeDp * 0.65f),
            glowAlpha = pulseAlpha
        )
    }
}

/**
 * Empty Library state logo.
 */
@Composable
fun XemuEmptyLibraryLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        XemuEmblem(
            modifier = Modifier.size(sizeDp),
            primaryColor = XemuCyan.copy(alpha = 0.4f),
            secondaryColor = XemuViolet.copy(alpha = 0.3f),
            glowAlpha = 0.4f
        )
    }
}

/**
 * Error state logo in alert amber/crimson neon.
 */
@Composable
fun XemuErrorLogo(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 80.dp
) {
    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        XemuEmblem(
            modifier = Modifier.size(sizeDp),
            primaryColor = XemuRed,
            secondaryColor = Color(0xFFF97316),
            glowAlpha = 0.9f
        )
    }
}
