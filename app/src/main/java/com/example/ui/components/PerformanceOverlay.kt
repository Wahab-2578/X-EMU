package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.TelemetryStats
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuGreen
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary

@Composable
fun PerformanceOverlay(
    stats: TelemetryStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xCC050811))
            .border(1.dp, XemuCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("performance_telemetry_overlay"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // FPS Indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (stats.fps >= 55) XemuGreen else Color(0xFFFBBF24))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "${stats.fps} FPS",
                color = XemuGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Frame Time
        Text(
            text = String.format("%.1f ms", stats.frameTimeMs),
            color = XemuCyan,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        // RAM Usage
        Text(
            text = "RAM: ${stats.ramUsedMb} MB",
            color = XemuTextPrimary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        // Temperature (or N/A)
        Text(
            text = "TEMP: ${stats.temperatureStr}",
            color = XemuTextSecondary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        // Renderer
        Text(
            text = stats.rendererStr,
            color = XemuCyan,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
