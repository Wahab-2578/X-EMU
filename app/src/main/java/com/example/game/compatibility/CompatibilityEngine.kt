package com.example.game.compatibility

import com.example.data.models.CompatibilityStatus
import com.example.data.models.Game

sealed class LaunchResult {
    data class Success(val runtimeSessionId: String, val renderer: String) : LaunchResult()
    data class Incompatible(val title: String, val reason: String, val suggestedAction: String) : LaunchResult()
    data class MissingFiles(val missingPath: String) : LaunchResult()
    data class ExecutableNotFound(val requestedExe: String) : LaunchResult()
    data class InsufficientStorage(val requiredBytes: Long, val availableBytes: Long) : LaunchResult()
    data class RuntimeError(val message: String) : LaunchResult()
}

data class SystemRequirementsCheck(
    val hasVulkanSupport: Boolean,
    val vulkanVersion: String,
    val openGlVersion: String,
    val availableRamMb: Long,
    val cpuArch: String,
    val is64Bit: Boolean,
    val supportsWineRuntime: Boolean
)

interface CompatibilityEngine {
    fun checkSystemCompatibility(): SystemRequirementsCheck
    suspend fun evaluateGameCompatibility(game: Game): CompatibilityStatus
    suspend fun launchGame(game: Game): LaunchResult
}
