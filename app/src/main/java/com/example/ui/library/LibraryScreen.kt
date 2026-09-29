package com.example.ui.library

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CompatibilityStatus
import com.example.data.models.Game
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.GameCard
import com.example.ui.components.GameCoverImage
import com.example.ui.home.EmptyLibraryState
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary

enum class LibraryViewMode {
    GRID,
    LARGE_CARD,
    COMPACT_LIST
}

enum class LibraryFilter(val label: String) {
    ALL("All"),
    INSTALLED("Installed"),
    RECENT("Recently Played"),
    FAVORITES("Favorites"),
    COMPATIBLE("Compatible"),
    UNKNOWN("Unknown")
}

enum class LibrarySort(val label: String) {
    NAME("Name"),
    RECENT("Recently Played"),
    DATE_ADDED("Date Added"),
    SIZE("Size")
}

@Composable
fun LibraryScreen(
    games: List<Game>,
    searchQuery: String,
    onSelectGame: (Game) -> Unit,
    onPlayGame: (Game) -> Unit,
    onToggleFavorite: (Game) -> Unit,
    onNavigateToAddGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(LibraryViewMode.GRID) }
    var currentFilter by remember { mutableStateOf(LibraryFilter.ALL) }
    var currentSort by remember { mutableStateOf(LibrarySort.RECENT) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter logic
    val filteredGames = games.filter { game ->
        val matchesSearch = searchQuery.isBlank() ||
                game.name.contains(searchQuery, ignoreCase = true) ||
                game.description.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (currentFilter) {
            LibraryFilter.ALL -> true
            LibraryFilter.INSTALLED -> true
            LibraryFilter.RECENT -> game.lastPlayed > 0
            LibraryFilter.FAVORITES -> game.favorite
            LibraryFilter.COMPATIBLE -> game.compatibilityStatus == CompatibilityStatus.PLAYABLE || game.compatibilityStatus == CompatibilityStatus.GOOD
            LibraryFilter.UNKNOWN -> game.compatibilityStatus == CompatibilityStatus.UNKNOWN
        }

        matchesSearch && matchesFilter
    }.let { list ->
        when (currentSort) {
            LibrarySort.NAME -> list.sortedBy { it.name.lowercase() }
            LibrarySort.RECENT -> list.sortedByDescending { it.lastPlayed }
            LibrarySort.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
            LibrarySort.SIZE -> list.sortedByDescending { it.fileSize }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .testTag("library_screen")
    ) {
        // Toolbar: Filters, View Toggles, Sorting, and Add Game button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filters horizontal row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                items(LibraryFilter.values()) { filter ->
                    val selected = currentFilter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { currentFilter = filter },
                        label = { Text(filter.label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = XemuCyan.copy(alpha = 0.2f),
                            selectedLabelColor = XemuCyan,
                            containerColor = XemuSurfaceVariant,
                            labelColor = XemuTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) XemuCyan else XemuCardBorder
                        ),
                        modifier = Modifier.height(32.dp).testTag("filter_${filter.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Toolbar Actions: Sort, View Switcher, Add Game
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sorting selector
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(XemuSurfaceVariant)
                            .border(1.dp, XemuCardBorder, RoundedCornerShape(8.dp))
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", tint = XemuCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(currentSort.label, color = XemuTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(XemuSurface)
                    ) {
                        LibrarySort.values().forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort.label, color = if (currentSort == sort) XemuCyan else XemuTextPrimary, fontSize = 12.sp) },
                                onClick = {
                                    currentSort = sort
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                // View Mode Switcher
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(XemuSurfaceVariant)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    IconButton(
                        onClick = { viewMode = LibraryViewMode.GRID },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Grid",
                            tint = if (viewMode == LibraryViewMode.GRID) XemuCyan else XemuTextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewMode = LibraryViewMode.LARGE_CARD },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewAgenda,
                            contentDescription = "Large Cards",
                            tint = if (viewMode == LibraryViewMode.LARGE_CARD) XemuCyan else XemuTextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewMode = LibraryViewMode.COMPACT_LIST },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewList,
                            contentDescription = "List",
                            tint = if (viewMode == LibraryViewMode.COMPACT_LIST) XemuCyan else XemuTextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Add Game Button
                Button(
                    onClick = onNavigateToAddGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XemuCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("library_add_game_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ ADD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // Library Content according to view mode
        if (filteredGames.isEmpty()) {
            EmptyLibraryState(onAddGame = onNavigateToAddGame, modifier = Modifier.weight(1f))
        } else {
            when (viewMode) {
                LibraryViewMode.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 210.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().testTag("library_grid_view")
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            GameCard(
                                game = game,
                                onSelect = { onSelectGame(game) },
                                onPlay = { onPlayGame(game) },
                                onToggleFavorite = { onToggleFavorite(game) },
                                cardWidth = 230,
                                cardHeight = 150
                            )
                        }
                    }
                }

                LibraryViewMode.LARGE_CARD -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 310.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().testTag("library_large_card_view")
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            GameCard(
                                game = game,
                                onSelect = { onSelectGame(game) },
                                onPlay = { onPlayGame(game) },
                                onToggleFavorite = { onToggleFavorite(game) },
                                cardWidth = 330,
                                cardHeight = 200
                            )
                        }
                    }
                }

                LibraryViewMode.COMPACT_LIST -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize().testTag("library_list_view")
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            CompactGameRow(
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
    }
}

@Composable
private fun CompactGameRow(
    game: Game,
    onSelect: () -> Unit,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(XemuSurface)
            .border(1.dp, XemuCardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                GameCoverImage(
                    coverPath = game.coverPath,
                    gameName = game.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = game.name,
                        color = XemuTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (game.isDemo) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(XemuCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("DEMO", color = XemuCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CompatibilityBadge(status = game.compatibilityStatus)
                    Text("•", color = XemuTextTertiary, fontSize = 10.sp)
                    Text(game.executablePath, color = XemuTextTertiary, fontSize = 11.sp, maxLines = 1)
                    Text("•", color = XemuTextTertiary, fontSize = 10.sp)
                    Text(game.fileSize, color = XemuTextTertiary, fontSize = 11.sp)
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (game.favorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (game.favorite) XemuRed else XemuTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(XemuCyan)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(18.dp))
            }
        }
    }
}
