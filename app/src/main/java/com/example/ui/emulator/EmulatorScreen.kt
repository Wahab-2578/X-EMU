package com.example.ui.emulator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.SystemTelemetry
import com.example.core.TelemetryStats
import com.example.data.models.Game
import com.example.ui.components.PerformanceOverlay
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuViolet
import kotlinx.coroutines.isActive

@Composable
fun EmulatorScreen(
    game: Game,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Game state simulation
    var isPaused by remember { mutableStateOf(false) }
    var touchControlsVisible by remember { mutableStateOf(game.touchControlsEnabled) }
    var showMouse by remember { mutableStateOf(false) }
    var showKeyboard by remember { mutableStateOf(false) }

    // Analog stick and inputs
    var stickX by remember { mutableFloatStateOf(0f) }
    var stickY by remember { mutableFloatStateOf(0f) }
    var boostActive by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    // Render loop stats
    var currentFps by remember { mutableIntStateOf(60) }
    var currentFrameTimeMs by remember { mutableFloatStateOf(16.6f) }
    var telemetry by remember { mutableStateOf(TelemetryStats()) }

    // Player position in virtual world
    var playerX by remember { mutableFloatStateOf(0.5f) }
    var gameWorldTick by remember { mutableLongStateOf(0L) }

    // 60 FPS Render loop and telemetry update
    LaunchedEffect(isPaused) {
        var frameCount = 0
        var lastFpsTime = System.currentTimeMillis()
        var lastFrameTimeNano = System.nanoTime()

        while (isActive && !isPaused) {
            withFrameNanos { now ->
                val dt = (now - lastFrameTimeNano) / 1_000_000f
                lastFrameTimeNano = now
                currentFrameTimeMs = dt.coerceIn(8f, 33f)

                // Update game world
                playerX = (playerX + stickX * 0.02f).coerceIn(0.15f, 0.85f)
                gameWorldTick++
                frameCount++

                val nowMs = System.currentTimeMillis()
                if (nowMs - lastFpsTime >= 500) {
                    currentFps = ((frameCount * 1000f) / (nowMs - lastFpsTime)).toInt().coerceIn(30, 60)
                    frameCount = 0
                    lastFpsTime = nowMs
                    telemetry = SystemTelemetry.getLiveTelemetry(
                        context = context,
                        currentFps = currentFps,
                        frameTimeMs = currentFrameTimeMs,
                        activeRenderer = game.graphicsBackend
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("emulator_fullscreen_screen")
    ) {
        // Real-time Interactive 60FPS Game Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Cyberpunk / Sci-fi Virtual World Horizon
            val horizonY = h * 0.42f

            // Sky gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF030712), Color(0xFF1E1145), Color(0xFF4C1D95)),
                    startY = 0f,
                    endY = horizonY
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w, horizonY)
            )

            // Neon synthwave sun at horizon
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEE00), Color(0xFFFF0077)),
                    startY = horizonY - 100f,
                    endY = horizonY + 20f
                ),
                radius = 65f,
                center = Offset(w / 2f, horizonY - 10f)
            )

            // 2. 3D Perspective Road / Grid floor
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0728), Color(0xFF050014)),
                    startY = horizonY,
                    endY = h
                ),
                topLeft = Offset(0f, horizonY),
                size = Size(w, h - horizonY)
            )

            // Road grid lines converging towards horizon
            val roadCenterX = w / 2f
            val roadTopWidth = w * 0.15f
            val roadBottomWidth = w * 0.75f

            // Road polygon
            val roadPath = Path().apply {
                moveTo(roadCenterX - roadTopWidth / 2f, horizonY)
                lineTo(roadCenterX + roadTopWidth / 2f, horizonY)
                lineTo(roadCenterX + roadBottomWidth / 2f, h)
                lineTo(roadCenterX - roadBottomWidth / 2f, h)
                close()
            }
            drawPath(
                path = roadPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1F1147), Color(0xFF090014))
                )
            )

            // Dynamic grid lines scrolling forward
            val offsetScroll = (gameWorldTick * (if (boostActive) 8f else 4f)) % 40f
            for (i in 0..12) {
                val progress = (i * 35f + offsetScroll) / 450f
                if (progress in 0f..1f) {
                    val lineY = horizonY + (progress * progress) * (h - horizonY)
                    val halfWidth = (roadTopWidth + (roadBottomWidth - roadTopWidth) * progress) / 2f
                    drawLine(
                        color = XemuCyan.copy(alpha = (progress * 0.7f).coerceIn(0.1f, 0.8f)),
                        start = Offset(roadCenterX - halfWidth, lineY),
                        end = Offset(roadCenterX + halfWidth, lineY),
                        strokeWidth = 2f * progress + 1f
                    )
                }
            }

            // Outer road borders
            drawLine(
                color = Color(0xFFEC4899),
                start = Offset(roadCenterX - roadTopWidth / 2f, horizonY),
                end = Offset(roadCenterX - roadBottomWidth / 2f, h),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFFEC4899),
                start = Offset(roadCenterX + roadTopWidth / 2f, horizonY),
                end = Offset(roadCenterX + roadBottomWidth / 2f, h),
                strokeWidth = 4f
            )

            // 3. Player Vehicle / Mech Avatar controlled by analog stick
            val vehicleX = w * playerX
            val vehicleY = h * 0.78f

            // Engine thrust glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (boostActive) Color(0xFF00FFFF) else Color(0xFFFF5500)),
                        Color.Transparent
                    ),
                    center = Offset(vehicleX, vehicleY + 28f),
                    radius = if (boostActive) 45f else 28f
                ),
                radius = if (boostActive) 45f else 28f,
                center = Offset(vehicleX, vehicleY + 28f)
            )

            // Futuristic vehicle body
            val vehiclePath = Path().apply {
                moveTo(vehicleX, vehicleY - 24f) // Nose
                lineTo(vehicleX + 32f, vehicleY + 20f) // Right wing
                lineTo(vehicleX + 16f, vehicleY + 24f)
                lineTo(vehicleX, vehicleY + 16f) // Engine center
                lineTo(vehicleX - 16f, vehicleY + 24f)
                lineTo(vehicleX - 32f, vehicleY + 20f) // Left wing
                close()
            }
            drawPath(path = vehiclePath, color = Color(0xFF1E293B))

            // Cockpit glass
            drawCircle(color = XemuCyan, radius = 7f, center = Offset(vehicleX, vehicleY - 4f))

            // Wing neon trims
            drawLine(
                color = if (boostActive) XemuCyan else Color(0xFFA855F7),
                start = Offset(vehicleX - 32f, vehicleY + 20f),
                end = Offset(vehicleX, vehicleY - 24f),
                strokeWidth = 2.5f
            )
            drawLine(
                color = if (boostActive) XemuCyan else Color(0xFFA855F7),
                start = Offset(vehicleX + 32f, vehicleY + 20f),
                end = Offset(vehicleX, vehicleY - 24f),
                strokeWidth = 2.5f
            )

            // HUD Crosshairs & telemetry targeting
            drawLine(color = XemuCyan.copy(alpha = 0.3f), start = Offset(vehicleX - 50f, vehicleY), end = Offset(vehicleX + 50f, vehicleY), strokeWidth = 1f)
            drawLine(color = XemuCyan.copy(alpha = 0.3f), start = Offset(vehicleX, vehicleY - 50f), end = Offset(vehicleX, vehicleY + 50f), strokeWidth = 1f)
        }

        // Top UI Bar: Telemetry Overlay + Pause & Toggle Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Telemetry overlay
            PerformanceOverlay(stats = telemetry)

            // Status feedback message if any
            if (statusMessage.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xBB000000))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(statusMessage, color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Control Actions: Toggle Touch, Pause
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { touchControlsVisible = !touchControlsVisible },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.Gamepad,
                        contentDescription = "Toggle Touch Controls",
                        tint = if (touchControlsVisible) XemuCyan else XemuTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { isPaused = true },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .testTag("emulator_pause_button")
                ) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = XemuTextPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Configurable Touch Controls Layer
        if (touchControlsVisible && !isPaused) {
            VirtualTouchControls(
                onAnalogMove = { x, y ->
                    stickX = x
                    stickY = y
                },
                onButtonPressed = { button ->
                    when (button) {
                        "A" -> {
                            boostActive = true
                            statusMessage = "TURBO BOOST ACTIVATED"
                        }
                        "B" -> {
                            boostActive = false
                            statusMessage = "BRAKE / DEFLECTOR"
                        }
                        "X" -> statusMessage = "FIRE SECONDARY / MISSILE"
                        "Y" -> statusMessage = "SWITCH WEAPON / REAR VIEW"
                        "START" -> isPaused = true
                        else -> statusMessage = "INPUT: $button"
                    }
                },
                onButtonReleased = { button ->
                    if (button == "A") boostActive = false
                },
                onToggleMouse = { showMouse = !showMouse },
                onToggleKeyboard = { showKeyboard = !showKeyboard }
            )
        }

        // Virtual Mouse Overlay
        if (showMouse && !isPaused) {
            VirtualMouseOverlay(
                onLeftClick = { x, y ->
                    statusMessage = "MOUSE LEFT CLICK @ (${x.toInt()}, ${y.toInt()})"
                },
                onRightClick = { x, y ->
                    statusMessage = "MOUSE RIGHT CLICK @ (${x.toInt()}, ${y.toInt()})"
                },
                onScroll = { delta ->
                    statusMessage = "SCROLL: ${if (delta > 0) "UP" else "DOWN"}"
                }
            )
        }

        // Virtual Keyboard Overlay
        if (showKeyboard && !isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                VirtualKeyboardOverlay(
                    onKeyPress = { key ->
                        statusMessage = "KEY PRESSED: $key"
                    },
                    onClose = { showKeyboard = false }
                )
            }
        }

        // Pause Menu Dialog Overlay
        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xDD050811))
                    .clickable { /* absorb taps */ }
                    .testTag("emulator_pause_menu"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(420.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(XemuSurface)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(18.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GAME PAUSED",
                        color = XemuTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.name,
                        color = XemuCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Menu Options
                    Button(
                        onClick = { isPaused = false },
                        colors = ButtonDefaults.buttonColors(containerColor = XemuCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("pause_resume_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESUME", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { statusMessage = "STATE SAVED TO SLOT 1"; isPaused = false },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = XemuTextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("QUICK SAVE STATE", color = XemuTextPrimary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { statusMessage = "STATE RESTORED"; isPaused = false },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = XemuTextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("QUICK LOAD STATE", color = XemuTextPrimary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onExitGame,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444), contentColor = XemuRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("pause_exit_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = XemuRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXIT TO GAME HUB", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
