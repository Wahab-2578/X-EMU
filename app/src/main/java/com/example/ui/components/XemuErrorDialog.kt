package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuRed
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary

data class XemuErrorState(
    val title: String,
    val message: String,
    val technicalDetails: String,
    val canOpenSettings: Boolean = true
)

@Composable
fun XemuErrorDialog(
    error: XemuErrorState,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(480.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(XemuSurface)
                .border(1.dp, XemuRed.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(22.dp)
                .testTag("xemu_error_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                XemuErrorLogo(sizeDp = 56.dp)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = error.title,
                    color = XemuTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error.message,
                    color = XemuTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (showDetails) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(XemuSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = error.technicalDetails,
                            color = XemuCyan,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("BACK", color = XemuTextPrimary, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showDetails = !showDetails },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.2f).height(40.dp)
                    ) {
                        Text(if (showDetails) "HIDE DETAILS" else "VIEW DETAILS", color = XemuTextSecondary, fontSize = 10.sp)
                    }

                    if (error.canOpenSettings) {
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenSettings()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = XemuCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.3f).height(40.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SETTINGS", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
