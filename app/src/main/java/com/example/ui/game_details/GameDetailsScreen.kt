package com.example.ui.game_details

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Game
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GameCoverImage
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GameDetailsScreen(
    game: Game,
    onPlay: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteGame: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateAddedStr = dateFormat.format(Date(game.dateAdded))
    val lastPlayedStr = if (game.lastPlayed > 0) dateFormat.format(Date(game.lastPlayed)) else "Never"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .testTag("game_details_screen")
    ) {
        // Large Background Artwork
        GameCoverImage(
            coverPath = game.coverPath,
            gameName = game.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66000000),
                            Color(0xCC090D16),
                            XemuBackground
                        ),
                        startY = 0f,
                        endY = 400f
                    )
                )
        )

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x88000000))
                    .testTag("details_back_button")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = XemuTextPrimary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .testTag("details_favorite_button")
                ) {
                    Icon(
                        imageVector = if (game.favorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (game.favorite) XemuRed else XemuTextPrimary
                    )
                }

                IconButton(
                    onClick = onDeleteGame,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .testTag("details_delete_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = XemuTextTertiary)
                }
            }
        }

        // Main Scrollable Details Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompatibilityBadge(status = game.compatibilityStatus)
                        if (game.isDemo) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(XemuCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("SAMPLE / DEMO", color = XemuCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = game.name,
                        color = XemuTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = game.description.ifBlank { "Legal PC game profile ready to launch inside the modular XEMU compatibility architecture." },
                        color = XemuTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                // Primary Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = XemuCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("details_play_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PLAY GAME", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenSettings,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("details_settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = XemuTextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SETTINGS", color = XemuTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Two-column layout for Info & Advanced Specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(XemuSurface)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("GAME INFORMATION", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailItem(label = "Launcher", value = game.executablePath)
                    DetailItem(label = "Storage Size", value = game.fileSize)
                    DetailItem(label = "Date Added", value = dateAddedStr)
                    DetailItem(label = "Last Played", value = lastPlayedStr)
                    DetailItem(label = "Path", value = game.folderPath.ifBlank { "Local Sandbox" })
                }

                // Advanced Specs & Engine Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(XemuSurface)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("ENGINE CONFIGURATION", color = XemuCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailItem(label = "Graphics Backend", value = game.graphicsBackend)
                    DetailItem(label = "Target Resolution", value = game.resolution)
                    DetailItem(label = "FPS Limit", value = "${game.fpsLimit} FPS")
                    DetailItem(label = "Controller Profile", value = game.controllerProfile)
                    DetailItem(label = "Touch Controls", value = if (game.touchControlsEnabled) "Enabled" else "Disabled")
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = XemuTextTertiary, fontSize = 12.sp)
        Text(
            text = value,
            color = XemuTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
