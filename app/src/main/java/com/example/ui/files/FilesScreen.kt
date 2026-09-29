package com.example.ui.files

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuGreen
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary

data class FileEntry(
    val name: String,
    val path: String,
    val size: String,
    val isFolder: Boolean,
    val dateModified: String
)

@Composable
fun FilesScreen(
    onNavigateToAddGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleDirectories = listOf(
        FileEntry("XEMU/Games/NeonOverdrive", "Internal/XEMU/Games/NeonOverdrive", "1.2 GB", true, "Today"),
        FileEntry("XEMU/Games/ChronoAssault", "Internal/XEMU/Games/ChronoAssault", "2.8 GB", true, "Yesterday"),
        FileEntry("XEMU/Games/VoidRunner", "Internal/XEMU/Games/VoidRunner", "450 MB", true, "Sep 22"),
        FileEntry("XEMU/Shaders/vk_pipeline_cache.bin", "Internal/XEMU/Shaders", "34 MB", false, "Today"),
        FileEntry("XEMU/Profiles/xbox360_mapping.cfg", "Internal/XEMU/Profiles", "4 KB", false, "Sep 15")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .padding(20.dp)
            .testTag("files_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Game Files & Storage",
                    color = XemuTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage game directories, archive packages, and runtime shader cache",
                    color = XemuTextTertiary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onNavigateToAddGame,
                colors = ButtonDefaults.buttonColors(containerColor = XemuCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ IMPORT FOLDER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Storage Overview Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(XemuSurface)
                .border(1.dp, XemuCardBorder, RoundedCornerShape(14.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(XemuCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("Internal Storage Index", color = XemuTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Total XEMU Library: 4.45 GB across 3 titles", color = XemuTextSecondary, fontSize = 11.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(XemuGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("SCOPED ACCESS GRANTED", color = XemuGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("INDEXED DIRECTORIES & PACKAGES", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sampleDirectories) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(XemuSurface)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            imageVector = if (file.isFolder) Icons.Default.Folder else Icons.Default.FolderZip,
                            contentDescription = null,
                            tint = if (file.isFolder) XemuCyan else XemuTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(file.name, color = XemuTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(file.path, color = XemuTextTertiary, fontSize = 10.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(file.size, color = XemuTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(file.dateModified, color = XemuTextTertiary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
