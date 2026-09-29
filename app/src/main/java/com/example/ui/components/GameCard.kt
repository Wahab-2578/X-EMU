package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.CompatibilityStatus
import com.example.data.models.Game
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCardBorderGlow
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuGreen
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet

@Composable
fun GameCard(
    game: Game,
    onSelect: () -> Unit,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Int = 220,
    cardHeight: Int = 160
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1.0f, label = "card_scale")

    Box(
        modifier = modifier
            .width(cardWidth.dp)
            .height(cardHeight.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(XemuSurface)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (isPressed) XemuCardBorderGlow else XemuCardBorder,
                        XemuCardBorder.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            )
            .testTag("game_card_${game.id}")
    ) {
        // Cover Artwork or Geometric Placeholder
        GameCoverImage(
            coverPath = game.coverPath,
            gameName = game.name,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x66000000),
                            Color(0xEE090D16)
                        ),
                        startY = 40f
                    )
                )
        )

        // Top badges: PC Badge & Favorite toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PC Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC000000))
                        .border(1.dp, XemuCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "PC Game",
                        tint = XemuCyan,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "PC",
                        color = XemuTextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (game.isDemo) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(XemuViolet.copy(alpha = 0.8f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DEMO",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Favorite Icon
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0x88000000))
            ) {
                Icon(
                    imageVector = if (game.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (game.favorite) XemuRed else XemuTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        // Bottom Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    color = XemuTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CompatibilityBadge(status = game.compatibilityStatus)
                    Text(
                        text = if (game.lastPlayed > 0) "Played" else "Ready",
                        color = XemuTextTertiary,
                        fontSize = 10.sp
                    )
                }
            }

            // Quick Play Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(XemuCyan)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GameCoverImage(
    coverPath: String,
    gameName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = when (coverPath) {
        "cover_neon_overdrive" -> context.resources.getIdentifier("cover_neon_overdrive", "drawable", context.packageName)
        "hero_cyber_assault" -> context.resources.getIdentifier("hero_cyber_assault", "drawable", context.packageName)
        else -> 0
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = gameName,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else if (coverPath.isNotBlank() && (coverPath.startsWith("content://") || coverPath.startsWith("file://") || coverPath.startsWith("/"))) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(coverPath).crossfade(true).build(),
            contentDescription = gameName,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        // Stylized geometric vector fallback cover
        Box(
            modifier = modifier
                .background(
                    Brush.radialGradient(
                        colors = listOf(XemuSurfaceVariant, Color(0xFF070B12))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                XemuEmblem(
                    modifier = Modifier.size(48.dp),
                    glowAlpha = 0.4f
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = gameName.take(15),
                    color = XemuTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CompatibilityBadge(status: CompatibilityStatus) {
    val (color, text) = when (status) {
        CompatibilityStatus.PLAYABLE -> XemuGreen to "Playable"
        CompatibilityStatus.GOOD -> XemuCyan to "Good"
        CompatibilityStatus.EXPERIMENTAL -> Color(0xFFF59E0B) to "Experimental"
        CompatibilityStatus.NOT_SUPPORTED -> XemuRed to "Unsupported"
        CompatibilityStatus.UNKNOWN -> XemuTextTertiary to "Unknown"
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 4.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
