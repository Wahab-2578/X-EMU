package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controller.ControllerManager
import com.example.data.database.AppDatabase
import com.example.data.models.Game
import com.example.data.repository.GameRepository
import com.example.data.repository.SettingsRepository
import com.example.game.compatibility.LaunchResult
import com.example.game.compatibility.NativeCompatibilityLayer
import com.example.ui.components.XemuErrorState
import com.example.ui.components.XemuNavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Splash : ScreenDestination()
    object MainHub : ScreenDestination()
    data class GameDetails(val gameId: Long) : ScreenDestination()
    data class Emulator(val gameId: Long) : ScreenDestination()
    object AddGame : ScreenDestination()
}

class XemuViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val gameRepository = GameRepository(db.gameDao())
    val settingsRepository = SettingsRepository(application)
    val controllerManager = ControllerManager(application)
    val compatibilityLayer = NativeCompatibilityLayer(application)

    val games: StateFlow<List<Game>> = gameRepository.allGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings = settingsRepository.settings

    // Navigation and screen state
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Splash)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _activeNavDestination = MutableStateFlow(XemuNavDestination.HOME)
    val activeNavDestination: StateFlow<XemuNavDestination> = _activeNavDestination.asStateFlow()

    private val _selectedGame = MutableStateFlow<Game?>(null)
    val selectedGame: StateFlow<Game?> = _selectedGame.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorState = MutableStateFlow<XemuErrorState?>(null)
    val errorState: StateFlow<XemuErrorState?> = _errorState.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showFirstRunDialog = MutableStateFlow(false)
    val showFirstRunDialog: StateFlow<Boolean> = _showFirstRunDialog.asStateFlow()

    private val _showPikoDialog = MutableStateFlow(false)
    val showPikoDialog: StateFlow<Boolean> = _showPikoDialog.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.ensureDefaultSampleGames()
            if (!settings.value.showStartupAnimation) {
                _currentScreen.value = ScreenDestination.MainHub
            }
        }
    }

    fun onStartupAnimationFinished() {
        _currentScreen.value = ScreenDestination.MainHub
    }

    fun navigateToHub(nav: XemuNavDestination = XemuNavDestination.HOME) {
        _activeNavDestination.value = nav
        _currentScreen.value = ScreenDestination.MainHub
    }

    fun selectGame(game: Game) {
        _selectedGame.value = game
        _currentScreen.value = ScreenDestination.GameDetails(game.id)
    }

    fun launchGame(game: Game) {
        viewModelScope.launch {
            when (val result = compatibilityLayer.launchGame(game)) {
                is LaunchResult.Success -> {
                    gameRepository.updateLastPlayed(game.id)
                    _selectedGame.value = game
                    _currentScreen.value = ScreenDestination.Emulator(game.id)
                }
                is LaunchResult.Incompatible -> {
                    _errorState.value = XemuErrorState(
                        title = result.title,
                        message = result.reason,
                        technicalDetails = result.suggestedAction
                    )
                }
                is LaunchResult.MissingFiles -> {
                    _errorState.value = XemuErrorState(
                        title = "Missing Files",
                        message = "Required game files could not be found at path: ${result.missingPath}",
                        technicalDetails = "Verify storage access permissions or ensure files were not moved from the imported folder."
                    )
                }
                is LaunchResult.ExecutableNotFound -> {
                    _errorState.value = XemuErrorState(
                        title = "Executable Not Found",
                        message = "No valid executable launcher was detected: ${result.requestedExe}",
                        technicalDetails = "Check game settings to choose an alternate launcher binary (.exe)."
                    )
                }
                is LaunchResult.InsufficientStorage -> {
                    _errorState.value = XemuErrorState(
                        title = "Insufficient Memory",
                        message = "Not enough available RAM to initialize the compatibility sandbox.",
                        technicalDetails = "Required: ~512MB RAM, Available: ${result.availableBytes / (1024 * 1024)}MB."
                    )
                }
                is LaunchResult.RuntimeError -> {
                    _errorState.value = XemuErrorState(
                        title = "Runtime Error",
                        message = result.message,
                        technicalDetails = "The compatibility translation subsystem encountered an unexpected exception."
                    )
                }
            }
        }
    }

    fun exitGameToDetails() {
        val curGame = _selectedGame.value
        if (curGame != null) {
            _currentScreen.value = ScreenDestination.GameDetails(curGame.id)
        } else {
            _currentScreen.value = ScreenDestination.MainHub
        }
    }

    fun navigateToAddGame() {
        _currentScreen.value = ScreenDestination.AddGame
    }

    fun importNewGame(game: Game) {
        viewModelScope.launch {
            val id = gameRepository.insertGame(game)
            val inserted = gameRepository.getGameByIdDirect(id)
            if (inserted != null) {
                _selectedGame.value = inserted
                _currentScreen.value = ScreenDestination.GameDetails(id)
            } else {
                _currentScreen.value = ScreenDestination.MainHub
            }
        }
    }

    fun updateGameSettings(game: Game) {
        viewModelScope.launch {
            gameRepository.updateGame(game)
            _selectedGame.value = game
            _showSettingsDialog.value = false
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            gameRepository.deleteGame(game)
            _selectedGame.value = null
            _currentScreen.value = ScreenDestination.MainHub
        }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            gameRepository.toggleFavorite(game.id, game.favorite)
            if (_selectedGame.value?.id == game.id) {
                _selectedGame.value = _selectedGame.value?.copy(favorite = !game.favorite)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openGameSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeGameSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun dismissError() {
        _errorState.value = null
    }

    fun dismissFirstRun() {
        _showFirstRunDialog.value = false
    }

    fun triggerFirstRun() {
        _showFirstRunDialog.value = true
    }

    fun openPikoDialog() {
        _showPikoDialog.value = true
    }

    fun closePikoDialog() {
        _showPikoDialog.value = false
    }

    fun resetDemoDatabase() {
        viewModelScope.launch {
            gameRepository.ensureDefaultSampleGames()
        }
    }

    override fun onCleared() {
        super.onCleared()
        controllerManager.unregister()
    }
}
