package com.example.core

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

data class TelemetryStats(
    val fps: Int = 60,
    val frameTimeMs: Float = 16.6f,
    val ramUsedMb: Long = 0,
    val ramTotalMb: Long = 0,
    val temperatureStr: String = "N/A",
    val rendererStr: String = "Vulkan 1.3"
)

object SystemTelemetry {

    fun getLiveTelemetry(context: Context, currentFps: Int, frameTimeMs: Float, activeRenderer: String): TelemetryStats {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)
        val usedRamMb = totalRamMb - availRamMb

        // Battery temperature via sticky intent
        var tempStr = "N/A"
        try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            if (temp > 0) {
                val celsius = temp / 10.0f
                tempStr = String.format("%.1f°C", celsius)
            }
        } catch (_: Exception) {
            tempStr = "N/A"
        }

        val rendererDisplay = if (activeRenderer.isNotBlank()) activeRenderer else "Vulkan 1.3"

        return TelemetryStats(
            fps = currentFps,
            frameTimeMs = frameTimeMs,
            ramUsedMb = usedRamMb,
            ramTotalMb = totalRamMb,
            temperatureStr = tempStr,
            rendererStr = rendererDisplay
        )
    }
}
