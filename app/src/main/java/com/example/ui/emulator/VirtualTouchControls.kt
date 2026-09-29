package com.example.ui.emulator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XemuCyan
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun VirtualTouchControls(
    opacity: Float = 0.75f,
    onAnalogMove: (x: Float, y: Float) -> Unit,
    onButtonPressed: (String) -> Unit,
    onButtonReleased: (String) -> Unit,
    onToggleMouse: () -> Unit,
    onToggleKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(opacity)
            .testTag("virtual_touch_controls")
    ) {
        // Top Shoulders: L1, L2 on left; R1, R2 on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Bumpers
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BumperButton(label = "L2", onClick = { onButtonPressed("L2") })
                BumperButton(label = "L1", onClick = { onButtonPressed("L1") })
            }

            // Center: Select, Start, Mouse, Keyboard
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleMouse,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x77000000))
                ) {
                    Icon(Icons.Default.Mouse, contentDescription = "Virtual Mouse", tint = XemuCyan, modifier = Modifier.size(18.dp))
                }

                SystemPillButton(label = "SELECT", onClick = { onButtonPressed("SELECT") })
                SystemPillButton(label = "START", onClick = { onButtonPressed("START") })

                IconButton(
                    onClick = onToggleKeyboard,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x77000000))
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = "Virtual Keyboard", tint = XemuCyan, modifier = Modifier.size(18.dp))
                }
            }

            // Right Bumpers
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BumperButton(label = "R1", onClick = { onButtonPressed("R1") })
                BumperButton(label = "R2", onClick = { onButtonPressed("R2") })
            }
        }

        // Bottom Left: Virtual Analog Stick
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        ) {
            VirtualJoystick(
                onMove = onAnalogMove
            )
        }

        // Bottom Right: A / B / X / Y Action Button Diamond
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            ActionButtonsDiamond(
                onPress = onButtonPressed
            )
        }
    }
}

@Composable
fun VirtualJoystick(
    onMove: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var stickOffsetX by remember { mutableFloatStateOf(0f) }
    var stickOffsetY by remember { mutableFloatStateOf(0f) }
    val maxRadius = 50f

    Box(
        modifier = modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color(0x55000000))
            .border(2.dp, Color(0x6600F0FF), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        stickOffsetX = 0f
                        stickOffsetY = 0f
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        stickOffsetX = 0f
                        stickOffsetY = 0f
                        onMove(0f, 0f)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val newX = stickOffsetX + dragAmount.x
                    val newY = stickOffsetY + dragAmount.y
                    val dist = kotlin.math.sqrt(newX * newX + newY * newY)
                    if (dist <= maxRadius) {
                        stickOffsetX = newX
                        stickOffsetY = newY
                    } else {
                        stickOffsetX = (newX / dist) * maxRadius
                        stickOffsetY = (newY / dist) * maxRadius
                    }
                    onMove(stickOffsetX / maxRadius, stickOffsetY / maxRadius)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Base markings
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color(0x3300F0FF), radius = 24f)
        }

        // Thumb knob
        Box(
            modifier = Modifier
                .offset(x = (stickOffsetX * 0.9f).dp, y = (stickOffsetY * 0.9f).dp)
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xAA182238))
                .border(2.dp, XemuCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(XemuCyan.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun ActionButtonsDiamond(
    onPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Y (Top) - Amber/Yellow
        RoundActionButton(
            label = "Y",
            color = Color(0xFFFBBF24),
            modifier = Modifier.align(Alignment.TopCenter),
            onClick = { onPress("Y") }
        )

        // X (Left) - Blue
        RoundActionButton(
            label = "X",
            color = Color(0xFF38BDF8),
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = { onPress("X") }
        )

        // B (Right) - Red
        RoundActionButton(
            label = "B",
            color = Color(0xFFF87171),
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { onPress("B") }
        )

        // A (Bottom) - Green
        RoundActionButton(
            label = "A",
            color = Color(0xFF34D399),
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { onPress("A") }
        )
    }
}

@Composable
private fun RoundActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0x88111827))
            .border(2.dp, color.copy(alpha = 0.8f), CircleShape)
            .clickable(onClick = onClick)
            .testTag("action_button_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun BumperButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 54.dp, height = 30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x88111827))
            .border(1.5.dp, Color(0x8800F0FF), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SystemPillButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 26.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x88111827))
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
