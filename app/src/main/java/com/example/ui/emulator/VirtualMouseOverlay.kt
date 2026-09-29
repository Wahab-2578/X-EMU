package com.example.ui.emulator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.XemuCyan
import kotlin.math.roundToInt

@Composable
fun VirtualMouseOverlay(
    sensitivity: Float = 1.0f,
    onLeftClick: (x: Float, y: Float) -> Unit,
    onRightClick: (x: Float, y: Float) -> Unit,
    onScroll: (deltaY: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var cursorX by remember { mutableFloatStateOf(400f) }
    var cursorY by remember { mutableFloatStateOf(240f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(sensitivity) {
                detectTransformGestures { _, pan, zoom, _ ->
                    cursorX = (cursorX + pan.x * sensitivity).coerceIn(10f, size.width.toFloat() - 10f)
                    cursorY = (cursorY + pan.y * sensitivity).coerceIn(10f, size.height.toFloat() - 10f)
                    if (zoom != 1f) {
                        onScroll(zoom - 1f)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onLeftClick(cursorX, cursorY)
                    },
                    onLongPress = {
                        onRightClick(cursorX, cursorY)
                    }
                )
            }
            .testTag("virtual_mouse_overlay")
    ) {
        // Render stylized PC mouse pointer at cursor position
        Canvas(
            modifier = Modifier
                .offset { IntOffset(cursorX.roundToInt(), cursorY.roundToInt()) }
                .size(24.dp)
        ) {
            val cursorPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, 32f)
                lineTo(8f, 24f)
                lineTo(16f, 36f)
                lineTo(22f, 32f)
                lineTo(14f, 20f)
                lineTo(24f, 20f)
                close()
            }
            // Black outline
            drawPath(path = cursorPath, color = Color.Black)
            // White/Cyan interior
            val innerPath = Path().apply {
                moveTo(2f, 3f)
                lineTo(2f, 28f)
                lineTo(8f, 22f)
                lineTo(15f, 33f)
                lineTo(19f, 30f)
                lineTo(12f, 19f)
                lineTo(20f, 19f)
                close()
            }
            drawPath(path = innerPath, color = XemuCyan)
        }
    }
}
