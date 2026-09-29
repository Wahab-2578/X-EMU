package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet

enum class XemuNavDestination(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    LIBRARY("My Games", Icons.Default.VideogameAsset),
    ADD_GAME("Add Game", Icons.Default.Add),
    FILES("Files", Icons.Default.FolderZip),
    CONTROLLERS("Controllers", Icons.Default.Gamepad),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun XemuSidebar(
    currentDestination: XemuNavDestination,
    onNavigate: (XemuNavDestination) -> Unit,
    onOpenPiko: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(88.dp)
            .background(XemuSurface)
            .border(width = 1.dp, color = XemuCardBorder.copy(alpha = 0.6f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Branding
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            XemuEmblem(
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onNavigate(XemuNavDestination.HOME) }
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "XEMU",
                color = XemuTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }

        // Navigation Items
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            XemuNavDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination
                SidebarItem(
                    destination = destination,
                    isSelected = isSelected,
                    onClick = { onNavigate(destination) }
                )
            }
        }

        // Bottom AI Bot Button (Piko)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(XemuCyan.copy(alpha = 0.12f))
                .border(1.dp, XemuCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenPiko)
                .padding(vertical = 6.dp)
                .testTag("sidebar_piko_button")
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "Piko AI Bot",
                tint = XemuCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "PIKO AI",
                color = XemuCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun SidebarItem(
    destination: XemuNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) XemuSurfaceVariant else Color.Transparent,
        label = "sidebar_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) XemuCyan else XemuTextSecondary,
        label = "sidebar_content"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("nav_item_${destination.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(XemuCyan.copy(alpha = 0.15f))
                )
            }
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = destination.label,
            color = if (isSelected) XemuTextPrimary else XemuTextTertiary,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
