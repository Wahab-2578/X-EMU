package com.example.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.StartupSoundSynthesizer
import com.example.ui.components.XemuEmblem
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val speed: Float
)

@Composable
fun SplashScreen(
    playAudio: Boolean = true,
    onAnimationComplete: () -> Unit
) {
    // Stages: 1=Dark+particles, 2=Forming streaks, 3=Emblem visible+pulse, 4=Text visible, 5=Chime plays, 6=Zoom forward & transition
    var stage by remember { mutableIntStateOf(1) }

    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0.0f) }
    val glowPulse = remember { Animatable(0.4f) }
    val lightSweep = remember { Animatable(0f) }

    // Particles system
    val particles = remember {
        List(40) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 1f,
                alpha = Random.nextFloat() * 0.6f + 0.2f,
                speed = Random.nextFloat() * 0.002f + 0.001f
            )
        }
    }

    LaunchedEffect(Unit) {
        // Stage 1: Dark screen, faint ambient glow appears (0 to 400ms)
        delay(350)
        stage = 2

        // Stage 2: Logo begins forming from streaks (400ms to 900ms)
        logoAlpha.animateTo(0.6f, animationSpec = tween(500, easing = LinearEasing))
        stage = 3

        // Stage 3: Logo becomes fully visible, glow pulse (900ms to 1400ms)
        launch {
            logoAlpha.animateTo(1.0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            glowPulse.animateTo(1.0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        logoScale.animateTo(1.0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        stage = 4

        // Stage 4: "XEMU" text appears (1400ms)
        delay(200)
        stage = 5

        // Stage 5: Short original electronic startup chime plays
        if (playAudio) {
            launch {
                StartupSoundSynthesizer.playStartupChime()
            }
        }

        // Light sweep across logo
        lightSweep.animateTo(1f, animationSpec = tween(600, easing = LinearEasing))
        delay(250)
        stage = 6

        // Stage 6: Zoom forward slightly and transition smoothly to Home
        logoScale.animateTo(1.15f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        delay(150)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(XemuBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Allow instant skip on click
                onAnimationComplete()
            }
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle futuristic ambient particles and background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Center ambient radial glow
            val glowRadius = w * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        XemuCyan.copy(alpha = 0.12f * glowPulse.value),
                        XemuViolet.copy(alpha = 0.06f * glowPulse.value),
                        Color.Transparent
                    ),
                    center = Offset(w / 2f, h / 2f),
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = Offset(w / 2f, h / 2f)
            )

            // Star/particle field
            for (p in particles) {
                val px = p.x * w
                val py = (p.y + (if (stage >= 2) lightSweep.value * 0.1f else 0f)) % 1f * h
                drawCircle(
                    color = Color.White.copy(alpha = p.alpha * (if (stage >= 2) 0.8f else 0.3f)),
                    radius = p.radius,
                    center = Offset(px, py)
                )
            }
        }

        // Animated Logo and Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale.value)
                .graphicsLayer(alpha = logoAlpha.value)
        ) {
            XemuEmblem(
                modifier = Modifier.size(110.dp),
                glowAlpha = glowPulse.value,
                glowWidthFactor = if (stage >= 3) 1.2f else 0.8f
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = stage >= 4,
                enter = fadeIn(animationSpec = tween(350))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "XEMU",
                        color = XemuTextPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 6.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PC GAMING COMPATIBILITY HUB",
                        color = XemuCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 3.sp
                    )
                }
            }
        }

        // Skip prompt in bottom corner
        Text(
            text = "TAP ANYWHERE TO SKIP",
            color = XemuTextTertiary,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}
