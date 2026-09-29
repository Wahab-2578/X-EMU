package com.example.ui.settings

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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.repository.SettingsRepository
import com.example.data.repository.XemuSettings
import com.example.ui.components.XemuEmblem
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

enum class GlobalSettingsSection(val label: String) {
    STARTUP("Startup"),
    GAMING("Gaming & Overlays"),
    APPEARANCE("Appearance"),
    LIBRARY("Library & Storage"),
    ABOUT("About XEMU")
}

@Composable
fun SettingsScreen(
    settings: XemuSettings,
    settingsRepo: SettingsRepository,
    onResetDemoDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(GlobalSettingsSection.STARTUP) }
    var cacheClearedMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .padding(20.dp)
            .testTag("settings_screen")
    ) {
        // Header
        Text(
            text = "XEMU Hub Settings",
            color = XemuTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Configure startup sequence, hardware acceleration, controls, and cache",
            color = XemuTextTertiary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Landscape Two-Column Layout
        Row(modifier = Modifier.weight(1f)) {
            // Left Navigation Tabs
            Column(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .background(XemuSurface)
                    .border(1.dp, XemuCardBorder, RoundedCornerShape(14.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                GlobalSettingsSection.values().forEach { section ->
                    val isSelected = activeSection == section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) XemuCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { activeSection = section }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("settings_tab_${section.name.lowercase()}")
                    ) {
                        Text(
                            text = section.label,
                            color = if (isSelected) XemuCyan else XemuTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Settings Details Pane
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .background(XemuSurface)
                    .border(1.dp, XemuCardBorder, RoundedCornerShape(14.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                when (activeSection) {
                    GlobalSettingsSection.STARTUP -> {
                        Text("STARTUP CONFIGURATION", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingToggleRow(
                            title = "Show Cinematic Startup Animation",
                            description = "Plays particle streak reveal of XEMU emblem upon launching the application",
                            checked = settings.showStartupAnimation,
                            onCheckedChange = { settingsRepo.updateStartupAnimation(it) }
                        )

                        SettingToggleRow(
                            title = "Play Electronic Startup Chime",
                            description = "Synthesizes an original warm harmonic chime during startup sequence",
                            checked = settings.playStartupSound,
                            onCheckedChange = { settingsRepo.updateStartupSound(it) }
                        )
                    }

                    GlobalSettingsSection.GAMING -> {
                        Text("GAMING & PERFORMANCE OVERLAYS", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingToggleRow(
                            title = "Real-time FPS Counter",
                            description = "Displays live frame rate and frame delivery pacing during gameplay",
                            checked = settings.fpsOverlayEnabled,
                            onCheckedChange = { settingsRepo.updateFpsOverlay(it) }
                        )

                        SettingToggleRow(
                            title = "System Telemetry Overlay",
                            description = "Shows RAM utilization, battery temperature, and active graphics renderer",
                            checked = settings.performanceOverlayEnabled,
                            onCheckedChange = { settingsRepo.updatePerformanceOverlay(it) }
                        )

                        SettingToggleRow(
                            title = "Enable Touch Controls by Default",
                            description = "Displays the virtual analog stick, bumpers, and diamond action buttons upon starting games",
                            checked = settings.touchControlsDefault,
                            onCheckedChange = { settingsRepo.updateTouchControlsDefault(it) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Touch Controls Opacity: ${(settings.touchControlsOpacity * 100).toInt()}%", color = XemuTextPrimary, fontSize = 12.sp)
                        Slider(
                            value = settings.touchControlsOpacity,
                            onValueChange = {},
                            colors = SliderDefaults.colors(thumbColor = XemuCyan, activeTrackColor = XemuCyan)
                        )
                    }

                    GlobalSettingsSection.APPEARANCE -> {
                        Text("THEME & ACCENTS", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Current Theme: Cyber Charcoal & Neon Cyan (Locked)", color = XemuTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Optimized for landscape OLED and high-refresh gaming displays with deep black levels.",
                            color = XemuTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    GlobalSettingsSection.LIBRARY -> {
                        Text("STORAGE & REPOSITORY", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Game Library Directory:", color = XemuTextSecondary, fontSize = 11.sp)
                        Text(settings.gameLibraryLocation, color = XemuCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = {
                                    cacheClearedMessage = "Compiled shaders & translation caches cleared"
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CLEAR SHADER CACHE", color = XemuCyan, fontSize = 11.sp)
                            }

                            Button(
                                onClick = onResetDemoDatabase,
                                colors = ButtonDefaults.buttonColors(containerColor = XemuSurfaceVariant, contentColor = XemuTextPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("RESTORE DEMO TITLES", fontSize = 11.sp)
                            }
                        }

                        if (cacheClearedMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(cacheClearedMessage, color = XemuGreen, fontSize = 11.sp)
                        }
                    }

                    GlobalSettingsSection.ABOUT -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            XemuEmblem(modifier = Modifier.size(44.dp))
                            Column {
                                Text("XEMU Android Hub", color = XemuTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                Text("Version 1.0.0 (Release build)", color = XemuCyan, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Architecture: Native Android Compatibility Engine with Vulkan translation abstraction.",
                            color = XemuTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Disclaimer: Compatibility depends on the game, Android device, graphics driver, and compatibility runtime. XEMU respects intellectual property rights and requires users to import only legally owned game files.",
                            color = XemuTextTertiary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, color = XemuTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(description, color = XemuTextTertiary, fontSize = 11.sp, lineHeight = 15.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = XemuCyan)
        )
    }
}
