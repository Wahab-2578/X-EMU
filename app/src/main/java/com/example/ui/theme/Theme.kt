package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val XemuDarkColorScheme = darkColorScheme(
    primary = XemuCyan,
    onPrimary = Color.Black,
    primaryContainer = XemuCyanDark,
    onPrimaryContainer = Color.White,
    secondary = XemuViolet,
    onSecondary = Color.White,
    secondaryContainer = XemuNeonPurple,
    onSecondaryContainer = Color.White,
    tertiary = XemuGreen,
    background = XemuBackground,
    onBackground = XemuTextPrimary,
    surface = XemuSurface,
    onSurface = XemuTextPrimary,
    surfaceVariant = XemuSurfaceVariant,
    onSurfaceVariant = XemuTextSecondary,
    outline = XemuCardBorder,
    error = XemuRed,
    onError = Color.White
)

@Composable
fun XemuTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = XemuDarkColorScheme,
        typography = Typography,
        content = content
    )
}
