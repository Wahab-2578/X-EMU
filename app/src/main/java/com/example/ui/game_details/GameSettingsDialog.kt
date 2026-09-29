package com.example.ui.game_details

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.Game
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary

enum class SettingsTab(val label: String) {
    GRAPHICS("Graphics"),
    PERFORMANCE("Performance"),
    CONTROLS("Controls"),
    AUDIO("Audio"),
    ADVANCED("Advanced")
}

@Composable
fun GameSettingsDialog(
    game: Game,
    onSave: (Game) -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(SettingsTab.GRAPHICS) }

    // State bindings
    var resolution by remember { mutableStateOf(game.resolution) }
    var graphicsBackend by remember { mutableStateOf(game.graphicsBackend) }
    var fpsLimit by remember { mutableIntStateOf(game.fpsLimit) }
    var touchControlsEnabled by remember { mutableStateOf(game.touchControlsEnabled) }
    var controllerProfile by remember { mutableStateOf(game.controllerProfile) }
    var masterVolume by remember { mutableFloatStateOf(1.0f) }
    var vSync by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(620.dp)
                .height(380.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(XemuSurface)
                .border(1.dp, XemuCardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
                .testTag("game_settings_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${game.name} Settings",
                            color = XemuTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Configure translation layers, renderer pipeline, and inputs",
                            color = XemuTextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = XemuTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs + Content in Row (Landscape friendly)
                Row(modifier = Modifier.weight(1f)) {
                    // Left Tab Navigation
                    Column(
                        modifier = Modifier
                            .width(130.dp)
                            .fillMaxHeight()
                            .background(XemuSurfaceVariant.copy(alpha = 0.5f))
                            .clip(RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SettingsTab.values().forEach { tab ->
                            val isSelected = activeTab == tab
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) XemuCyan.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { activeTab = tab }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = tab.label,
                                    color = if (isSelected) XemuCyan else XemuTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Tab Content Body
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (activeTab) {
                            SettingsTab.GRAPHICS -> {
                                Text("GRAPHICS & RENDERING", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                DropdownOption(
                                    label = "Graphics Backend",
                                    currentValue = graphicsBackend,
                                    options = listOf("Vulkan 1.3 (Turnip)", "Vulkan 1.3 (System)", "OpenGL ES 3.2", "D3D11 to Vulkan (DXVK)"),
                                    onSelect = { graphicsBackend = it }
                                )

                                DropdownOption(
                                    label = "Target Resolution",
                                    currentValue = resolution,
                                    options = listOf("720p (1280x720)", "1080p (1920x1080)", "Native Display", "540p Performance"),
                                    onSelect = { resolution = it }
                                )

                                SettingToggle(
                                    label = "VSync (Vertical Sync)",
                                    checked = vSync,
                                    onCheckedChange = { vSync = it }
                                )
                            }

                            SettingsTab.PERFORMANCE -> {
                                Text("PERFORMANCE & LIMITS", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                DropdownOption(
                                    label = "Frame Rate Target",
                                    currentValue = "$fpsLimit FPS",
                                    options = listOf("30 FPS", "60 FPS", "90 FPS", "120 FPS", "Uncapped"),
                                    onSelect = { fpsLimit = it.replace(" FPS", "").replace("Uncapped", "144").toIntOrNull() ?: 60 }
                                )

                                SettingToggle(
                                    label = "Real-time FPS Overlay",
                                    checked = true,
                                    onCheckedChange = {}
                                )
                            }

                            SettingsTab.CONTROLS -> {
                                Text("INPUT CONFIGURATION", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                SettingToggle(
                                    label = "On-Screen Touch Controls",
                                    checked = touchControlsEnabled,
                                    onCheckedChange = { touchControlsEnabled = it }
                                )

                                DropdownOption(
                                    label = "Active Controller Profile",
                                    currentValue = controllerProfile,
                                    options = listOf("Xbox 360 Standard", "DualShock 4 Profile", "DirectInput Generic", "Keyboard/Mouse Mapping"),
                                    onSelect = { controllerProfile = it }
                                )
                            }

                            SettingsTab.AUDIO -> {
                                Text("AUDIO MIXER", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Master Audio Volume: ${(masterVolume * 100).toInt()}%", color = XemuTextPrimary, fontSize = 12.sp)
                                Slider(
                                    value = masterVolume,
                                    onValueChange = { masterVolume = it },
                                    colors = SliderDefaults.colors(thumbColor = XemuCyan, activeTrackColor = XemuCyan)
                                )
                            }

                            SettingsTab.ADVANCED -> {
                                Text("COMPATIBILITY ENVIRONMENT", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                DropdownOption(
                                    label = "Windows Emulation Profile",
                                    currentValue = "Windows 10 x64",
                                    options = listOf("Windows 10 x64", "Windows 7 x64", "Windows XP SP3"),
                                    onSelect = {}
                                )

                                Text(
                                    text = "Compatibility depends on the game, Android device, graphics driver, and compatibility runtime.",
                                    color = XemuTextTertiary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Bottom Save & Cancel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text("CANCEL", color = XemuTextPrimary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val updated = game.copy(
                                resolution = resolution,
                                graphicsBackend = graphicsBackend,
                                fpsLimit = fpsLimit,
                                touchControlsEnabled = touchControlsEnabled,
                                controllerProfile = controllerProfile
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = XemuCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_game_settings_button")
                    ) {
                        Text("SAVE CHANGES", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownOption(
    label: String,
    currentValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(label, color = XemuTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(XemuSurfaceVariant)
                    .border(1.dp, XemuCardBorder, RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentValue, color = XemuTextPrimary, fontSize = 12.sp)
                Text("▼", color = XemuCyan, fontSize = 9.sp)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(XemuSurface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = if (option == currentValue) XemuCyan else XemuTextPrimary, fontSize = 12.sp) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = XemuTextPrimary, fontSize = 12.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = XemuCyan
            )
        )
    }
}
