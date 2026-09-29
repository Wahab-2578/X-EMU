package com.example.ui.add_game

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CompatibilityStatus
import com.example.data.models.Game
import com.example.game.scanner.GameFileScanner
import com.example.game.scanner.ScanResult
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.XemuLoadingLogo
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuCardBorder
import com.example.ui.theme.XemuCyan
import com.example.ui.theme.XemuSurface
import com.example.ui.theme.XemuSurfaceVariant
import com.example.ui.theme.XemuTextPrimary
import com.example.ui.theme.XemuTextSecondary
import com.example.ui.theme.XemuTextTertiary
import com.example.ui.theme.XemuViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddGameScreen(
    onGameImported: (Game) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scanner = remember { GameFileScanner(context) }

    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }

    // Form edit states
    var gameName by remember { mutableStateOf("") }
    var selectedExecutable by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedSize by remember { mutableStateOf("1.5 GB") }
    var folderPath by remember { mutableStateOf("") }
    var showExeDropdown by remember { mutableStateOf(false) }

    // SAF Document Tree Picker
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isScanning = true
                delay(800) // Realistic scanning effect
                val result = scanner.scanDocumentTree(uri)
                scanResult = result
                gameName = result.folderName
                selectedExecutable = result.selectedExecutable
                estimatedSize = result.formattedSize
                folderPath = uri.toString()
                description = "Imported PC game folder from storage."
                isScanning = false
            }
        }
    }

    // Single File / Exe Picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "Game.exe"
            val derivedName = fileName.substringBeforeLast(".").replace("_", " ")
            scanResult = ScanResult(
                folderName = derivedName,
                folderPath = uri.toString(),
                executables = listOf(fileName),
                selectedExecutable = fileName,
                totalSizeBytes = 1024 * 1024 * 750,
                formattedSize = "750 MB"
            )
            gameName = derivedName
            selectedExecutable = fileName
            estimatedSize = "750 MB"
            folderPath = uri.toString()
            description = "Imported executable package."
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(XemuBackground)
            .testTag("add_game_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("add_game_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = XemuTextPrimary)
                }
                Text(
                    text = "Add PC Game",
                    color = XemuTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isScanning) {
                // Scanning Progress Animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(XemuSurface)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        XemuLoadingLogo(sizeDp = 70.dp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Scanning Game Files...",
                            color = XemuTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Detecting executable binaries, assets, and game architecture",
                            color = XemuTextTertiary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (scanResult == null) {
                // Initial State: Options to select folder, file, or quick-import demo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option 1: Storage Access Framework Folder Picker
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(XemuSurface)
                            .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                            .clickable { folderPickerLauncher.launch(null) }
                            .padding(24.dp)
                            .testTag("select_game_folder_button")
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(XemuCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Select Game Folder",
                                color = XemuTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Choose a legally obtained PC game folder from your device's internal storage or SD card via SAF.",
                                color = XemuTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { folderPickerLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = XemuCyan, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("BROWSE FOLDER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    // Option 2: Select Executable (.exe / .bat)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(XemuSurface)
                            .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                            .clickable { filePickerLauncher.launch(arrayOf("*/*")) }
                            .padding(24.dp)
                            .testTag("select_executable_button")
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(XemuViolet.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Gamepad, contentDescription = null, tint = XemuViolet, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Select Executable File",
                                color = XemuTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select a standalone .exe or launcher script directly to create a custom profile.",
                                color = XemuTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("SELECT .EXE", color = XemuTextPrimary, fontSize = 11.sp)
                            }
                        }
                    }

                    // Option 3: Quick Test Import (Simulation)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(XemuSurface)
                            .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                isScanning = true
                                coroutineScope.launch {
                                    delay(600)
                                    scanResult = ScanResult(
                                        folderName = "OpenArena PC Demo",
                                        folderPath = "Internal/XEMU/Games/OpenArena",
                                        executables = listOf("openarena.exe", "dedicated.exe", "renderer_vk.exe"),
                                        selectedExecutable = "openarena.exe",
                                        totalSizeBytes = 1024L * 1024L * 520L,
                                        formattedSize = "520 MB"
                                    )
                                    gameName = "OpenArena PC Demo"
                                    selectedExecutable = "openarena.exe"
                                    estimatedSize = "520 MB"
                                    folderPath = "Internal/XEMU/Games/OpenArena"
                                    description = "Open-source 3D arena action game for testing PC translation."
                                    isScanning = false
                                }
                            }
                            .padding(24.dp)
                            .testTag("quick_import_demo_button")
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Scan Sample Directory",
                                color = XemuTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Test the scanner and executable selector using a pre-populated open-source test package.",
                                color = XemuTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    isScanning = true
                                    coroutineScope.launch {
                                        delay(600)
                                        scanResult = ScanResult(
                                            folderName = "OpenArena PC Demo",
                                            folderPath = "Internal/XEMU/Games/OpenArena",
                                            executables = listOf("openarena.exe", "dedicated.exe", "renderer_vk.exe"),
                                            selectedExecutable = "openarena.exe",
                                            totalSizeBytes = 1024L * 1024L * 520L,
                                            formattedSize = "520 MB"
                                        )
                                        gameName = "OpenArena PC Demo"
                                        selectedExecutable = "openarena.exe"
                                        estimatedSize = "520 MB"
                                        folderPath = "Internal/XEMU/Games/OpenArena"
                                        description = "Open-source 3D arena action game for testing PC translation."
                                        isScanning = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("QUICK TEST", color = Color(0xFF10B981), fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Legal & Compatibility Notice Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(XemuSurfaceVariant)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = XemuCyan, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Legal & Scoped Storage Compliance",
                            color = XemuTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "XEMU does not package or distribute commercial proprietary games. Please only import games you legally own. Files are scanned securely without requesting broad storage permissions.",
                            color = XemuTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            } else {
                // Game Detected Form (Section 7)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(XemuSurface)
                        .border(1.dp, XemuCardBorder, RoundedCornerShape(16.dp))
                        .padding(24.dp)
                        .testTag("game_detected_form")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Game Detected",
                                        color = XemuCyan,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    CompatibilityBadge(status = CompatibilityStatus.PLAYABLE)
                                }
                                Text(
                                    text = "Verify metadata and select the launcher binary to build the profile",
                                    color = XemuTextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "Est. Size: $estimatedSize",
                                color = XemuTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Game Name Input
                        OutlinedTextField(
                            value = gameName,
                            onValueChange = { gameName = it },
                            label = { Text("Game Name", color = XemuTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = XemuTextPrimary,
                                unfocusedTextColor = XemuTextPrimary,
                                focusedBorderColor = XemuCyan,
                                unfocusedBorderColor = XemuCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("import_game_name_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Executable Selector
                        Column {
                            Text(
                                text = "Selected Executable Launcher",
                                color = XemuTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(XemuSurfaceVariant)
                                    .border(1.dp, XemuCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { showExeDropdown = true }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedExecutable.ifBlank { "Select an executable..." },
                                    color = XemuCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("CHANGE", color = XemuTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            DropdownMenu(
                                expanded = showExeDropdown,
                                onDismissRequest = { showExeDropdown = false },
                                modifier = Modifier.background(XemuSurface)
                            ) {
                                val exes = scanResult?.executables ?: emptyList()
                                for (exe in exes) {
                                    DropdownMenuItem(
                                        text = { Text(exe, color = if (exe == selectedExecutable) XemuCyan else XemuTextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            selectedExecutable = exe
                                            showExeDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description / Notes", color = XemuTextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = XemuTextPrimary,
                                unfocusedTextColor = XemuTextPrimary,
                                focusedBorderColor = XemuCyan,
                                unfocusedBorderColor = XemuCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("import_game_description_input")
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action Buttons: IMPORT GAME / CANCEL
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedButton(
                                onClick = { scanResult = null },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("import_cancel_button")
                            ) {
                                Text("CANCEL", color = XemuTextPrimary, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val newGame = Game(
                                        name = gameName.ifBlank { "PC Game" },
                                        executablePath = selectedExecutable.ifBlank { "Game.exe" },
                                        folderPath = folderPath,
                                        coverPath = scanResult?.detectedCoverUri ?: "",
                                        iconPath = "ic_xemu_logo",
                                        description = description,
                                        fileSize = estimatedSize,
                                        dateAdded = System.currentTimeMillis(),
                                        lastPlayed = 0L,
                                        favorite = false,
                                        compatibilityStatus = CompatibilityStatus.PLAYABLE,
                                        isDemo = false,
                                        graphicsBackend = "Vulkan 1.3",
                                        resolution = "720p (1280x720)",
                                        fpsLimit = 60
                                    )
                                    onGameImported(newGame)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = XemuCyan, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(2f)
                                    .height(44.dp)
                                    .testTag("import_confirm_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("IMPORT GAME", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
