package com.example.ui.controllers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controller.ControllerInfo
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuGreen
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet

@Composable
fun ControllersScreen(
    controllers: List<ControllerInfo>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProfile by remember { mutableStateOf("Xbox 360 Standard") }
    var deadzone by remember { mutableFloatStateOf(0.12f) }
    var vibrationStrength by remember { mutableFloatStateOf(0.8f) }
    var activeTestButton by remember { mutableStateOf("Ready to Test") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("controllers_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Controller Management",
                    color = XemuTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bluetooth and USB HID gamepad detection & button mapping",
                    color = XemuTextTertiary,
                    fontSize = 12.sp
                )
            }

            OutlinedButton(
                onClick = onRefresh,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SCAN DEVICES", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Two Column Landscape Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: Connected Devices & Status
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(XemuSurface)
                    .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("CONNECTED CONTROLLERS", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (controllers.isNotEmpty()) XemuGreen.copy(alpha = 0.2f) else XemuSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (controllers.isNotEmpty()) "CONNECTED" else "DISCONNECTED",
                            color = if (controllers.isNotEmpty()) XemuGreen else XemuTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (controllers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(XemuSurfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Gamepad, contentDescription = null, tint = XemuTextTertiary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No physical gamepads detected", color = XemuTextSecondary, fontSize = 12.sp)
                            Text("Connect a Bluetooth or USB controller to map physical inputs", color = XemuTextTertiary, fontSize = 10.sp)
                        }
                    }
                } else {
                    controllers.forEach { controller ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(XemuSurfaceVariant)
                                .border(1.dp, XemuCardBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(XemuGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Gamepad, contentDescription = null, tint = XemuGreen, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text(controller.name, color = XemuTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Vendor ID: 0x${controller.vendorId.toString(16).padStart(4, '0')}", color = XemuTextTertiary, fontSize = 10.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(XemuGreen)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Tester
                Text("INPUT RESPONSE TESTER", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(XemuSurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(activeTestButton, color = XemuTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("A", "B", "X", "Y", "LB", "RB").forEach { btn ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33000000))
                                    .border(1.dp, XemuCardBorder, RoundedCornerShape(6.dp))
                                    .clickable { activeTestButton = "Button $btn Detected (Response: 4ms)" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(btn, color = XemuTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Right Column: Profiles & Calibration
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(XemuSurface)
                    .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Text("CONTROLLER PROFILES", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                listOf("Xbox 360 Standard", "PlayStation DualShock 4", "Nintendo Pro Controller", "Retro DirectInput 8-Way").forEach { profile ->
                    val isSelected = selectedProfile == profile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) XemuCyan.copy(alpha = 0.15f) else XemuSurfaceVariant)
                            .border(1.dp, if (isSelected) XemuCyan else XemuCardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedProfile = profile }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(profile, color = if (isSelected) XemuCyan else XemuTextPrimary, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Analog Stick Deadzone Slider
                Text("Stick Deadzone: ${(deadzone * 100).toInt()}%", color = XemuTextPrimary, fontSize = 11.sp)
                Slider(
                    value = deadzone,
                    onValueChange = { deadzone = it },
                    valueRange = 0.05f..0.35f,
                    colors = SliderDefaults.colors(thumbColor = XemuCyan, activeTrackColor = XemuCyan)
                )

                // Vibration Haptics Slider
                Text("Haptic Feedback Intensity: ${(vibrationStrength * 100).toInt()}%", color = XemuTextPrimary, fontSize = 11.sp)
                Slider(
                    value = vibrationStrength,
                    onValueChange = { vibrationStrength = it },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = XemuCyan, activeTrackColor = XemuCyan)
                )
            }
        }
    }
}
