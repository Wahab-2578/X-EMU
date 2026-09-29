package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ScreenDestination
import com.example.ui.XemuViewModel
import com.example.ui.add_game.AddGameScreen
import com.example.ui.components.XemuErrorDialog
import com.example.ui.components.XemuNavDestination
import com.example.ui.components.XemuSidebar
import com.example.ui.components.XemuTopBar
import com.example.ui.controllers.ControllersScreen
import com.example.ui.emulator.EmulatorScreen
import com.example.ui.files.FilesScreen
import com.example.ui.game_details.GameDetailsScreen
import com.example.ui.game_details.GameSettingsDialog
import com.example.ui.home.FirstRunDialog
import com.example.ui.home.HomeScreen
import com.example.ui.library.LibraryScreen
import com.example.ui.piko.PikoBotDialog
import com.example.ui.settings.SettingsScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.XemuBackground
import com.example.ui.theme.XemuTheme

class MainActivity : ComponentActivity() {

    private val viewModel: XemuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Strictly lock to landscape orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()

        setContent {
            XemuTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val activeNav by viewModel.activeNavDestination.collectAsState()
                val games by viewModel.games.collectAsState()
                val settings by viewModel.settings.collectAsState()
                val selectedGame by viewModel.selectedGame.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val isControllerConnected by viewModel.controllerManager.isControllerConnected.collectAsState()
                val controllers by viewModel.controllerManager.connectedControllers.collectAsState()
                val errorState by viewModel.errorState.collectAsState()
                val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
                val showFirstRunDialog by viewModel.showFirstRunDialog.collectAsState()
                val showPikoDialog by viewModel.showPikoDialog.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(XemuBackground)
                        .safeDrawingPadding()
                ) {
                    when (val screen = currentScreen) {
                        is ScreenDestination.Splash -> {
                            SplashScreen(
                                playAudio = settings.playStartupSound,
                                onAnimationComplete = { viewModel.onStartupAnimationFinished() }
                            )
                        }

                        is ScreenDestination.MainHub -> {
                            Row(modifier = Modifier.fillMaxSize()) {
                                // Left Landscape Sidebar
                                XemuSidebar(
                                    currentDestination = activeNav,
                                    onNavigate = { destination ->
                                        viewModel.navigateToHub(destination)
                                    },
                                    onOpenPiko = { viewModel.openPikoDialog() }
                                )

                                // Main Content Area with TopBar
                                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                                    XemuTopBar(
                                        title = when (activeNav) {
                                            XemuNavDestination.HOME -> "Game Hub"
                                            XemuNavDestination.LIBRARY -> "Game Library"
                                            XemuNavDestination.ADD_GAME -> "Add PC Game"
                                            XemuNavDestination.FILES -> "Files & Storage"
                                            XemuNavDestination.CONTROLLERS -> "Controllers"
                                            XemuNavDestination.SETTINGS -> "Settings"
                                        },
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                        isControllerConnected = isControllerConnected,
                                        controllerCount = controllers.size,
                                        onOpenControllers = { viewModel.navigateToHub(XemuNavDestination.CONTROLLERS) },
                                        onOpenSettings = { viewModel.navigateToHub(XemuNavDestination.SETTINGS) },
                                        onOpenPiko = { viewModel.openPikoDialog() }
                                    )

                                    when (activeNav) {
                                        XemuNavDestination.HOME -> {
                                            HomeScreen(
                                                games = games,
                                                onSelectGame = { viewModel.selectGame(it) },
                                                onPlayGame = { viewModel.launchGame(it) },
                                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                                onNavigateToAddGame = { viewModel.navigateToAddGame() },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        XemuNavDestination.LIBRARY -> {
                                            LibraryScreen(
                                                games = games,
                                                searchQuery = searchQuery,
                                                onSelectGame = { viewModel.selectGame(it) },
                                                onPlayGame = { viewModel.launchGame(it) },
                                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                                onNavigateToAddGame = { viewModel.navigateToAddGame() },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        XemuNavDestination.ADD_GAME -> {
                                            AddGameScreen(
                                                onGameImported = { viewModel.importNewGame(it) },
                                                onBack = { viewModel.navigateToHub(XemuNavDestination.HOME) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        XemuNavDestination.FILES -> {
                                            FilesScreen(
                                                onNavigateToAddGame = { viewModel.navigateToAddGame() },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        XemuNavDestination.CONTROLLERS -> {
                                            ControllersScreen(
                                                controllers = controllers,
                                                onRefresh = { viewModel.controllerManager.refreshControllers() },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        XemuNavDestination.SETTINGS -> {
                                            SettingsScreen(
                                                settings = settings,
                                                settingsRepo = viewModel.settingsRepository,
                                                onResetDemoDatabase = { viewModel.resetDemoDatabase() },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is ScreenDestination.GameDetails -> {
                            BackHandler {
                                viewModel.navigateToHub(XemuNavDestination.HOME)
                            }

                            val game = games.firstOrNull { it.id == screen.gameId } ?: selectedGame
                            if (game != null) {
                                GameDetailsScreen(
                                    game = game,
                                    onPlay = { viewModel.launchGame(game) },
                                    onOpenSettings = { viewModel.openGameSettingsDialog() },
                                    onToggleFavorite = { viewModel.toggleFavorite(game) },
                                    onDeleteGame = { viewModel.deleteGame(game) },
                                    onBack = { viewModel.navigateToHub(XemuNavDestination.HOME) }
                                )
                            }
                        }

                        is ScreenDestination.Emulator -> {
                            BackHandler {
                                viewModel.exitGameToDetails()
                            }

                            val game = games.firstOrNull { it.id == screen.gameId } ?: selectedGame
                            if (game != null) {
                                EmulatorScreen(
                                    game = game,
                                    onExitGame = { viewModel.exitGameToDetails() }
                                )
                            }
                        }

                        is ScreenDestination.AddGame -> {
                            BackHandler {
                                viewModel.navigateToHub(XemuNavDestination.HOME)
                            }

                            AddGameScreen(
                                onGameImported = { viewModel.importNewGame(it) },
                                onBack = { viewModel.navigateToHub(XemuNavDestination.HOME) }
                            )
                        }
                    }

                    // Global Error Dialog
                    errorState?.let { err ->
                        XemuErrorDialog(
                            error = err,
                            onDismiss = { viewModel.dismissError() },
                            onOpenSettings = {
                                viewModel.dismissError()
                                viewModel.openGameSettingsDialog()
                            }
                        )
                    }

                    // Game Settings Dialog
                    if (showSettingsDialog && selectedGame != null) {
                        GameSettingsDialog(
                            game = selectedGame!!,
                            onSave = { updated -> viewModel.updateGameSettings(updated) },
                            onDismiss = { viewModel.closeGameSettingsDialog() }
                        )
                    }

                    // First-Run Experience Dialog
                    if (showFirstRunDialog) {
                        FirstRunDialog(
                            onDismiss = { viewModel.dismissFirstRun() },
                            onChooseLibraryLocation = { viewModel.navigateToHub(XemuNavDestination.SETTINGS) },
                            onAddFirstGame = { viewModel.navigateToAddGame() }
                        )
                    }

                    // Piko AI Bot Dialog
                    if (showPikoDialog) {
                        PikoBotDialog(
                            onDismiss = { viewModel.closePikoDialog() }
                        )
                    }
                }
            }
        }
    }
}
