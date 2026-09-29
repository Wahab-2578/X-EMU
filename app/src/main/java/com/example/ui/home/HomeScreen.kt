package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Game
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GameCard
import com.example.ui.components.GameCoverImage
import com.example.ui.components.XemuEmptyLibraryLogo
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet

@Composable
fun HomeScreen(
    games: List<Game>,
    onSelectGame: (Game) -> Unit,
    onPlayGame: (Game) -> Unit,
    onToggleFavorite: (Game) -> Unit,
    onNavigateToAddGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (games.isEmpty()) {
        EmptyLibraryState(onAddGame = onNavigateToAddGame, modifier = modifier)
        return
    }

    // Featured game is the most recently played or first demo
    val featuredGame = games.maxByOrNull { it.lastPlayed } ?: games.first()
    val recentlyPlayed = games.filter { it.lastPlayed > 0 }.sortedByDescending { it.lastPlayed }
    val allOtherGames = games

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Featured Game Section
        item {
            HeroSection(
                game = featuredGame,
                onPlay = { onPlayGame(featuredGame) },
                onDetails = { onSelectGame(featuredGame) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Section: Recently Played
        if (recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader(title = "Recently Played", count = recentlyPlayed.size)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(recentlyPlayed, key = { "recent_${it.id}" }) { game ->
                        GameCard(
                            game = game,
                            onSelect = { onSelectGame(game) },
                            onPlay = { onPlayGame(game) },
                            onToggleFavorite = { onToggleFavorite(game) }
                        )
                    }
                }
            }
        }

        // Section: My Games
        item {
            SectionHeader(
                title = "My Games",
                count = allOtherGames.size,
                actionText = "+ ADD PC GAME",
                onAction = onNavigateToAddGame
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(allOtherGames, key = { "all_${it.id}" }) { game ->
                    GameCard(
                        game = game,
                        onSelect = { onSelectGame(game) },
                        onPlay = { onPlayGame(game) },
                        onToggleFavorite = { onToggleFavorite(game) }
                    )
                }
            }
        }

        // Section: Available / Compatible Games
        item {
            val compatibleGames = games.filter { it.isDemo }
            SectionHeader(title = "Available / Compatible Demos", count = compatibleGames.size)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(compatibleGames, key = { "compat_${it.id}" }) { game ->
                    GameCard(
                        game = game,
                        onSelect = { onSelectGame(game) },
                        onPlay = { onPlayGame(game) },
                        onToggleFavorite = { onToggleFavorite(game) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    game: Game,
    onPlay: () -> Unit,
    onDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(XemuSurface)
            .border(1.dp, XemuCardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onDetails)
            .testTag("home_hero_section")
    ) {
        // Hero Background Cover Art
        GameCoverImage(
            coverPath = game.coverPath,
            gameName = game.name,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Shroud for text clarity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xF00A0E17),
                            Color(0xBB0A0E17),
                            Color(0x33000000)
                        ),
                        startX = 0f,
                        endX = 900f
                    )
                )
        )

        // Hero Info & Actions
        Column(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(XemuCyan.copy(alpha = 0.2f))
                            .border(1.dp, XemuCyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "FEATURED TITLE",
                            color = XemuCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    CompatibilityBadge(status = game.compatibilityStatus)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = game.name,
                    color = XemuTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = game.description.ifBlank { "High-performance PC title with Vulkan hardware acceleration and customizable gamepad controls." },
                    color = XemuTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Buttons: Play and Details
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                        .height(42.dp)
                        .testTag("hero_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAY NOW",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                OutlinedButton(
                    onClick = onDetails,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = XemuTextPrimary),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("hero_details_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = XemuTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DETAILS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = XemuTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(XemuSurfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count",
                    color = XemuCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                color = XemuCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onAction)
                    .testTag("section_action_${title.lowercase().replace(" ", "_")}")
            )
        }
    }
}

@Composable
fun EmptyLibraryState(
    onAddGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .padding(32.dp)
            .testTag("empty_library_state"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        XemuEmptyLibraryLogo(sizeDp = 90.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Games Added",
            color = XemuTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Import a legally obtained PC game folder to get started.",
            color = XemuTextSecondary,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAddGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = XemuCyan,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(46.dp)
                .testTag("empty_state_add_game_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "+ ADD PC GAME",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
