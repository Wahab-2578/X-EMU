package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class XemuSettings(
    val showStartupAnimation: Boolean = true,
    val playStartupSound: Boolean = true,
    val fpsOverlayEnabled: Boolean = true,
    val performanceOverlayEnabled: Boolean = true,
    val touchControlsDefault: Boolean = true,
    val touchControlsOpacity: Float = 0.75f,
    val libraryLayout: String = "GRID", // GRID, CARD, LIST
    val defaultSorting: String = "RECENT",
    val gameLibraryLocation: String = "Internal / XEMU / Games",
    val graphicsBackend: String = "Vulkan 1.3 (Turnip)",
    val mouseSensitivity: Float = 1.0f,
    val controllerProfile: String = "Xbox 360 Standard"
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("xemu_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<XemuSettings> = _settings.asStateFlow()

    private fun loadSettings(): XemuSettings {
        return XemuSettings(
            showStartupAnimation = prefs.getBoolean("showStartupAnimation", true),
            playStartupSound = prefs.getBoolean("playStartupSound", true),
            fpsOverlayEnabled = prefs.getBoolean("fpsOverlayEnabled", true),
            performanceOverlayEnabled = prefs.getBoolean("performanceOverlayEnabled", true),
            touchControlsDefault = prefs.getBoolean("touchControlsDefault", true),
            touchControlsOpacity = prefs.getFloat("touchControlsOpacity", 0.75f),
            libraryLayout = prefs.getString("libraryLayout", "GRID") ?: "GRID",
            defaultSorting = prefs.getString("defaultSorting", "RECENT") ?: "RECENT",
            gameLibraryLocation = prefs.getString("gameLibraryLocation", "Internal / XEMU / Games") ?: "Internal / XEMU / Games",
            graphicsBackend = prefs.getString("graphicsBackend", "Vulkan 1.3 (Turnip)") ?: "Vulkan 1.3 (Turnip)",
            mouseSensitivity = prefs.getFloat("mouseSensitivity", 1.0f),
            controllerProfile = prefs.getString("controllerProfile", "Xbox 360 Standard") ?: "Xbox 360 Standard"
        )
    }

    fun updateStartupAnimation(enabled: Boolean) {
        prefs.edit().putBoolean("showStartupAnimation", enabled).apply()
        _settings.value = _settings.value.copy(showStartupAnimation = enabled)
    }

    fun updateStartupSound(enabled: Boolean) {
        prefs.edit().putBoolean("playStartupSound", enabled).apply()
        _settings.value = _settings.value.copy(playStartupSound = enabled)
    }

    fun updateFpsOverlay(enabled: Boolean) {
        prefs.edit().putBoolean("fpsOverlayEnabled", enabled).apply()
        _settings.value = _settings.value.copy(fpsOverlayEnabled = enabled)
    }

    fun updatePerformanceOverlay(enabled: Boolean) {
        prefs.edit().putBoolean("performanceOverlayEnabled", enabled).apply()
        _settings.value = _settings.value.copy(performanceOverlayEnabled = enabled)
    }

    fun updateTouchControlsDefault(enabled: Boolean) {
        prefs.edit().putBoolean("touchControlsDefault", enabled).apply()
        _settings.value = _settings.value.copy(touchControlsDefault = enabled)
    }

    fun updateLibraryLayout(layout: String) {
        prefs.edit().putString("libraryLayout", layout).apply()
        _settings.value = _settings.value.copy(libraryLayout = layout)
    }

    fun updateLibraryLocation(path: String) {
        prefs.edit().putString("gameLibraryLocation", path).apply()
        _settings.value = _settings.value.copy(gameLibraryLocation = path)
    }

    fun updateMouseSensitivity(sensitivity: Float) {
        prefs.edit().putFloat("mouseSensitivity", sensitivity).apply()
        _settings.value = _settings.value.copy(mouseSensitivity = sensitivity)
    }

    fun updateControllerProfile(profile: String) {
        prefs.edit().putString("controllerProfile", profile).apply()
        _settings.value = _settings.value.copy(controllerProfile = profile)
    }
}
