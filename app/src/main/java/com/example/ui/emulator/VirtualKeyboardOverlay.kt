package com.example.ui.emulator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary

@Composable
fun VirtualKeyboardOverlay(
    onKeyPress: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color(0xF00F172A))
            .border(1.dp, XemuCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(8.dp)
            .testTag("virtual_keyboard_overlay")
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VIRTUAL PC GAMING KEYBOARD",
                color = XemuCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close keyboard", tint = XemuTextSecondary, modifier = Modifier.size(16.dp))
            }
        }

        // Row 1: Function keys & ESC
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyButton(label = "ESC", modifier = Modifier.weight(1.2f), onClick = { onKeyPress("ESC") })
            KeyButton(label = "F1", modifier = Modifier.weight(1f), onClick = { onKeyPress("F1") })
            KeyButton(label = "F2", modifier = Modifier.weight(1f), onClick = { onKeyPress("F2") })
            KeyButton(label = "F3", modifier = Modifier.weight(1f), onClick = { onKeyPress("F3") })
            KeyButton(label = "F4", modifier = Modifier.weight(1f), onClick = { onKeyPress("F4") })
            KeyButton(label = "F5", modifier = Modifier.weight(1f), onClick = { onKeyPress("F5") })
            KeyButton(label = "F6", modifier = Modifier.weight(1f), onClick = { onKeyPress("F6") })
            KeyButton(label = "~", modifier = Modifier.weight(0.9f), onClick = { onKeyPress("~") })
            KeyButton(label = "DEL", modifier = Modifier.weight(1.2f), onClick = { onKeyPress("DEL") })
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 2: Numbers row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyButton(label = "TAB", modifier = Modifier.weight(1.2f), onClick = { onKeyPress("TAB") })
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").forEach { num ->
                KeyButton(label = num, modifier = Modifier.weight(1f), onClick = { onKeyPress(num) })
            }
            KeyButton(label = "BKSP", modifier = Modifier.weight(1.4f), onClick = { onKeyPress("BKSP") })
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 3: QWERTY + Gaming cluster
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").forEach { char ->
                val isWasd = char in listOf("W")
                KeyButton(label = char, isHighlighted = isWasd, modifier = Modifier.weight(1f), onClick = { onKeyPress(char) })
            }
            KeyButton(label = "ENT", modifier = Modifier.weight(1.5f), onClick = { onKeyPress("ENTER") })
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 4: ASDF & Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyButton(label = "SHIFT", modifier = Modifier.weight(1.5f), onClick = { onKeyPress("SHIFT") })
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").forEach { char ->
                val isWasd = char in listOf("A", "S", "D")
                KeyButton(label = char, isHighlighted = isWasd, modifier = Modifier.weight(1f), onClick = { onKeyPress(char) })
            }
            KeyButton(label = "▲", modifier = Modifier.weight(1f), onClick = { onKeyPress("UP") })
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 5: Space, Alt, Ctrl, and Arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyButton(label = "CTRL", modifier = Modifier.weight(1.2f), onClick = { onKeyPress("CTRL") })
            KeyButton(label = "ALT", modifier = Modifier.weight(1.2f), onClick = { onKeyPress("ALT") })
            KeyButton(label = "SPACE", modifier = Modifier.weight(4.5f), onClick = { onKeyPress("SPACE") })
            KeyButton(label = "◀", modifier = Modifier.weight(1f), onClick = { onKeyPress("LEFT") })
            KeyButton(label = "▼", modifier = Modifier.weight(1f), onClick = { onKeyPress("DOWN") })
            KeyButton(label = "▶", modifier = Modifier.weight(1f), onClick = { onKeyPress("RIGHT") })
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHighlighted) XemuCyan.copy(alpha = 0.25f) else XemuSurfaceVariant)
            .border(
                1.dp,
                if (isHighlighted) XemuCyan else XemuCardBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isHighlighted) XemuCyan else XemuTextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
