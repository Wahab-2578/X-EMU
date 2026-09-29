package com.example.ui.piko

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.ChatMessage
import com.example.ai.MessageSender
import com.example.ai.PikoAiService
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuGreen
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet
import kotlinx.coroutines.launch

@Composable
fun PikoAvatar(
    modifier: Modifier = Modifier,
    sizeDp: Int = 36
) {
    val infiniteTransition = rememberInfiniteTransition(label = "piko_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        XemuCyan.copy(alpha = glowAlpha),
                        XemuViolet.copy(alpha = 0.4f),
                        Color(0xFF0F172A)
                    )
                )
            )
            .border(1.5.dp, XemuCyan, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "Piko AI Bot",
            tint = Color.White,
            modifier = Modifier.size((sizeDp * 0.58f).dp)
        )
    }
}

@Composable
fun PikoBotDialog(
    onDismiss: () -> Unit
) {
    val pikoService = remember { PikoAiService() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = MessageSender.PIKO,
                text = "Hello! Main hoon Piko 🤖! XEMU ka personal AI guide. Agar aap new user hain aur kuch samajh nahi aa raha, toh aap mujhse be-jhijhak pooch sakte hain!"
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val quickQuestions = listOf(
        "Khel kaisay shuru karun?",
        "PC game import kaisay karein?",
        "Touch controls & mouse kaisay chalayein?",
        "Bluetooth controller connect kaisay hoga?",
        "Vulkan Turnip driver kya hai?"
    )

    fun sendMessage(textToSend: String) {
        if (textToSend.isBlank() || isThinking) return

        val userMsg = ChatMessage(sender = MessageSender.USER, text = textToSend.trim())
        messages.add(userMsg)
        inputText = ""
        isThinking = true

        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val pikoReply = pikoService.askPiko(userMsg.text, messages)
            messages.add(ChatMessage(sender = MessageSender.PIKO, text = pikoReply))
            isThinking = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(680.dp)
                .height(390.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(XemuSurface)
                .border(1.dp, XemuCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(16.dp)
                .testTag("piko_bot_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PikoAvatar(sizeDp = 40)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Piko AI Bot",
                                    color = XemuTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(XemuCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text("ASSISTANT", color = XemuCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(XemuGreen)
                                )
                                Text(
                                    text = "Ready to guide new users • English & Roman Urdu",
                                    color = XemuTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                messages.clear()
                                messages.add(
                                    ChatMessage(
                                        sender = MessageSender.PIKO,
                                        text = "Chat cleared! Main hoon Piko. Aap mujhse koi bhi game ya XEMU setting ka sawal pooch sakte hain!"
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = XemuTextTertiary, modifier = Modifier.size(18.dp))
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = XemuTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Questions Carousel for instant one-tap asking
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickQuestions) { question ->
                        SuggestionChip(
                            onClick = { sendMessage(question) },
                            label = { Text(question, fontSize = 10.sp, color = XemuTextPrimary) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(12.dp)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = XemuSurfaceVariant.copy(alpha = 0.8f)
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = XemuCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Chat Messages Scrollable Area
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF070B12))
                        .padding(12.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        ChatBubble(message = message)
                    }

                    if (isThinking) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = XemuCyan
                                )
                                Text(
                                    text = "Piko soch raha hai...",
                                    color = XemuCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Field & Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text("Piko se kuch bhi poochein (e.g. Game kaisay import karein?)...", color = XemuTextTertiary, fontSize = 12.sp)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = XemuTextPrimary,
                            unfocusedTextColor = XemuTextPrimary,
                            focusedBorderColor = XemuCyan,
                            unfocusedBorderColor = XemuCardBorder,
                            cursorColor = XemuCyan
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("piko_chat_input")
                    )

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (inputText.isNotBlank() && !isThinking) XemuCyan else XemuSurfaceVariant)
                            .clickable(enabled = inputText.isNotBlank() && !isThinking) {
                                sendMessage(inputText)
                            }
                            .testTag("piko_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !isThinking) Color.Black else XemuTextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            PikoAvatar(sizeDp = 26, modifier = Modifier.padding(end = 8.dp, top = 2.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.75f else 0.85f)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(Color(0xFF0090A8), Color(0xFF0284C7)))
                    else Brush.linearGradient(listOf(XemuSurfaceVariant, Color(0xFF1E293B)))
                )
                .border(
                    1.dp,
                    if (isUser) XemuCyan.copy(alpha = 0.6f) else XemuCardBorder,
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 12.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                if (!isUser) {
                    Text(
                        text = "Piko 🤖",
                        color = XemuCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
