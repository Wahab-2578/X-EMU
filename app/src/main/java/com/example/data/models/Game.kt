package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CompatibilityStatus(val label: String) {
    UNKNOWN("Unknown"),
    EXPERIMENTAL("Experimental"),
    PLAYABLE("Playable"),
    GOOD("Good"),
    NOT_SUPPORTED("Unsupported")
}

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val executablePath: String,
    val folderPath: String,
    val coverPath: String = "",
    val iconPath: String = "",
    val description: String = "",
    val fileSize: String = "Unknown",
    val dateAdded: Long = System.currentTimeMillis(),
    val lastPlayed: Long = 0L,
    val favorite: Boolean = false,
    val compatibilityStatus: CompatibilityStatus = CompatibilityStatus.UNKNOWN,
    val isDemo: Boolean = false,
    val graphicsBackend: String = "Vulkan 1.3 (Turnip)",
    val resolution: String = "720p (1280x720)",
    val fpsLimit: Int = 60,
    val touchControlsEnabled: Boolean = true,
    val controllerProfile: String = "Xbox 360 Standard"
)
