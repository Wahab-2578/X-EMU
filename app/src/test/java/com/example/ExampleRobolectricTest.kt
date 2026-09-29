package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.models.CompatibilityStatus
import com.example.data.models.Game
import com.example.data.repository.GameRepository
import com.example.data.repository.SettingsRepository
import com.example.game.compatibility.NativeCompatibilityLayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies XEMU name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("XEMU", appName)
    }

    @Test
    fun `settings repository loads default values correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SettingsRepository(context)
        val settings = repo.settings.value
        assertTrue(settings.showStartupAnimation)
        assertTrue(settings.playStartupSound)
        assertTrue(settings.fpsOverlayEnabled)
        assertEquals("GRID", settings.libraryLayout)
    }

    @Test
    fun `compatibility layer returns system requirements`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val layer = NativeCompatibilityLayer(context)
        val check = layer.checkSystemCompatibility()
        assertTrue(check.hasVulkanSupport)
        assertNotNull(check.cpuArch)
    }

    @Test
    fun `sample games are populated and queryable`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repo = GameRepository(db.gameDao())
        repo.ensureDefaultSampleGames()
        val games = repo.allGames.first()
        assertTrue(games.isNotEmpty())
        assertTrue(games.any { it.name.contains("Neon Overdrive") })
    }
}
