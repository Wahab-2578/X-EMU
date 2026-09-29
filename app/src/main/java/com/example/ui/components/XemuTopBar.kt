package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuGreen
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet

@Composable
fun XemuTopBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isControllerConnected: Boolean,
    controllerCount: Int,
    onOpenControllers: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPiko: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(XemuSurface.copy(alpha = 0.85f))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title or Search Field
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (!searchExpanded) {
                Text(
                    text = title,
                    color = XemuTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            } else {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search games, demos...", color = XemuTextTertiary, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = XemuTextPrimary,
                        unfocusedTextColor = XemuTextPrimary,
                        focusedBorderColor = XemuCyan,
                        unfocusedBorderColor = XemuCardBorder,
                        cursorColor = XemuCyan
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(44.dp)
                        .testTag("top_bar_search_input"),
                    trailingIcon = {
                        IconButton(onClick = {
                            onSearchQueryChange("")
                            searchExpanded = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close search", tint = XemuTextSecondary)
                        }
                    }
                )
            }
        }

        // Action controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search button toggle
            if (!searchExpanded) {
                IconButton(
                    onClick = { searchExpanded = true },
                    modifier = Modifier.testTag("top_bar_search_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = XemuTextSecondary
                    )
                }
            }

            // Piko AI Bot Quick Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(XemuCyan.copy(alpha = 0.15f))
                    .border(1.dp, XemuCyan.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onOpenPiko)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_bar_piko_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Ask Piko AI",
                    tint = XemuCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PIKO AI",
                    color = XemuCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Controller Status Indicator
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(XemuSurfaceVariant)
                    .border(
                        1.dp,
                        if (isControllerConnected) XemuGreen.copy(alpha = 0.5f) else XemuCardBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable(onClick = onOpenControllers)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_bar_controller_badge"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = if (isControllerConnected) XemuGreen else XemuTextTertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isControllerConnected) XemuGreen else XemuTextTertiary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isControllerConnected) "CONNECTED ($controllerCount)" else "NO CONTROLLER",
                    color = if (isControllerConnected) XemuTextPrimary else XemuTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("top_bar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = XemuTextSecondary
                )
            }
        }
    }
}
