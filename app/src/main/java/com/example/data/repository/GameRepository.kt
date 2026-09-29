package com.example.data.repository

import com.example.data.database.GameDao
import com.example.data.models.CompatibilityStatus
import com.example.data.models.Game
import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    val allGames: Flow<List<Game>> = gameDao.getAllGames()

    fun getGameById(id: Long): Flow<Game?> = gameDao.getGameById(id)

    suspend fun getGameByIdDirect(id: Long): Game? = gameDao.getGameByIdDirect(id)

    suspend fun insertGame(game: Game): Long = gameDao.insertGame(game)

    suspend fun updateGame(game: Game) = gameDao.updateGame(game)

    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)

    suspend fun deleteGameById(id: Long) = gameDao.deleteGameById(id)

    suspend fun updateLastPlayed(id: Long, timestamp: Long = System.currentTimeMillis()) {
        gameDao.updateLastPlayed(id, timestamp)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        gameDao.updateFavorite(id, !currentFavorite)
    }

    suspend fun ensureDefaultSampleGames() {
        if (gameDao.getGameCount() == 0) {
            val sampleGames = listOf(
                Game(
                    name = "Neon Overdrive 2088",
                    executablePath = "NeonOverdrive.exe",
                    folderPath = "Internal/XEMU/Demos/NeonOverdrive",
                    coverPath = "cover_neon_overdrive",
                    iconPath = "ic_xemu_logo",
                    description = "Public domain high-speed synthwave racing demo designed for testing Vulkan rendering pipeline and input responsiveness.",
                    fileSize = "1.2 GB",
                    dateAdded = System.currentTimeMillis() - 86400000L * 2,
                    lastPlayed = System.currentTimeMillis() - 3600000L * 4,
                    favorite = true,
                    compatibilityStatus = CompatibilityStatus.GOOD,
                    isDemo = true,
                    graphicsBackend = "Vulkan 1.3",
                    resolution = "720p (1280x720)",
                    fpsLimit = 60
                ),
                Game(
                    name = "Chrono Assault: Mech Wars",
                    executablePath = "ChronoAssault_x64.exe",
                    folderPath = "Internal/XEMU/Demos/ChronoAssault",
                    coverPath = "hero_cyber_assault",
                    iconPath = "ic_xemu_logo",
                    description = "Open-source sci-fi tactical action demo. Features 3D environment rendering, audio mixer verification, and controller mapping.",
                    fileSize = "2.8 GB",
                    dateAdded = System.currentTimeMillis() - 86400000L * 5,
                    lastPlayed = System.currentTimeMillis() - 86400000L * 1,
                    favorite = false,
                    compatibilityStatus = CompatibilityStatus.PLAYABLE,
                    isDemo = true,
                    graphicsBackend = "Vulkan 1.3 (Turnip)",
                    resolution = "720p (1280x720)",
                    fpsLimit = 60
                ),
                Game(
                    name = "Void Runner 3D",
                    executablePath = "VoidRunner.exe",
                    folderPath = "Internal/XEMU/Demos/VoidRunner",
                    coverPath = "",
                    iconPath = "ic_xemu_logo",
                    description = "Free developer test suite showcasing multi-threaded CPU translation and 60 FPS frame pacing.",
                    fileSize = "450 MB",
                    dateAdded = System.currentTimeMillis() - 86400000L * 7,
                    lastPlayed = 0L,
                    favorite = false,
                    compatibilityStatus = CompatibilityStatus.GOOD,
                    isDemo = true,
                    graphicsBackend = "OpenGL ES 3.2",
                    resolution = "Native",
                    fpsLimit = 60
                )
            )
            gameDao.insertGames(sampleGames)
        }
    }
}
