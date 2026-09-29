package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    PIKO
}

class PikoAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun askPiko(
        userMessage: String,
        chatHistory: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // If key is missing or dummy placeholder, use smart local contextual response
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflinePikoResponse(userMessage)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val systemInstructionText = """
                You are Piko, the friendly, super-smart, and welcoming AI companion for the XEMU PC Game Hub on Android.
                Your purpose is to help new users who feel confused, don't understand how XEMU works, or need assistance importing and playing games.
                Be encouraging, clear, and concise. You can speak English, Urdu, Roman Urdu, and other languages naturally if the user asks in them.
                Important XEMU facts:
                1. XEMU is an Android app running exclusively in landscape orientation.
                2. Adding games: Users tap '+ ADD PC GAME', select a legal PC game folder using Android's file/folder picker (Storage Access Framework), choose the main .exe, and XEMU creates a profile.
                3. Pre-loaded Demos: Neon Overdrive 2088 (Vulkan synthwave racer), Chrono Assault: Mech Wars (tactical sci-fi), and Void Runner 3D can be played right away without any external files!
                4. Touch controls: Virtual analog stick (left), A/B/X/Y action buttons (right), L1/L2/R1/R2 bumpers, and pause/start. Controls can be toggled on/off.
                5. Virtual Mouse & Keyboard: Mouse mode supports tap for left-click, long press for right-click. A full gaming keyboard with ESC, WASD, and arrows can be toggled during gameplay.
                6. Physical controllers: Bluetooth & USB HID gamepads (Xbox, DualShock, Generic) are detected automatically.
                7. Graphics backends: Vulkan 1.3 (Turnip driver), OpenGL ES 3.2, D3D11 to Vulkan (DXVK).
                Always introduce yourself cheerfully as Piko!
            """.trimIndent()

            val contentsArray = JSONArray()

            // Include last 6 turns for context
            val recentHistory = chatHistory.takeLast(6)
            for (msg in recentHistory) {
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                val partObj = JSONObject().put("text", msg.text)
                val partsArray = JSONArray().put(partObj)
                contentsArray.put(JSONObject().put("role", role).put("parts", partsArray))
            }

            // Current prompt
            val currentParts = JSONArray().put(JSONObject().put("text", userMessage))
            contentsArray.put(JSONObject().put("role", "user").put("parts", currentParts))

            val systemInstruction = JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstruction)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                })
            }

            val requestBody = requestJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext getOfflinePikoResponse(userMessage)
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                getOfflinePikoResponse(userMessage)
            }
        } catch (_: Exception) {
            getOfflinePikoResponse(userMessage)
        }
    }

    private fun getOfflinePikoResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("khel") || lower.contains("play") || lower.contains("start") || lower.contains("shuru") -> {
                "Hello! Main hoon Piko 🤖! Khel shuru karne ke liye:\n" +
                        "1. Agar aapke paas PC game files nahi hain, toh Home screen par pre-loaded demo games ('Neon Overdrive' ya 'Chrono Assault') par 'PLAY NOW' dabayein.\n" +
                        "2. Game screen khulte hi virtual joystick aur buttons se aap control kar sakte hain!\n" +
                        "Aapko kisi aur cheez mein madad chahiye?"
            }
            lower.contains("add") || lower.contains("import") || lower.contains("folder") || lower.contains("exe") -> {
                "Piko yahan hai aapki madad ke liye! 🎮\n" +
                        "Naya PC game add karne ke liye:\n" +
                        "1. Sidebar ya Home par '+ ADD PC GAME' button dabayein.\n" +
                        "2. 'Select Game Folder' par click karke apne device ka folder chunein.\n" +
                        "3. XEMU automatically .exe file detect karega. Sahi launcher select karein aur 'IMPORT GAME' par click karein!\n" +
                        "Bas, game aapki library mein save ho jayega!"
            }
            lower.contains("controller") || lower.contains("gamepad") || lower.contains("joystick") || lower.contains("bluetooth") -> {
                "Controller setup karna bohat aasan hai! 🕹️\n" +
                        "1. Apne phone ki Bluetooth settings mein jakar apna Xbox ya PlayStation controller pair karein.\n" +
                        "2. XEMU mein sidebar se 'Controllers' section kholen.\n" +
                        "3. Yahan aapko live controller status aur button response test mil jayega!"
            }
            lower.contains("mouse") || lower.contains("keyboard") || lower.contains("control") -> {
                "Controls ke bare mein Piko ka guide! 🖱️⌨️\n" +
                        "- **Touch Controls**: Left side par Virtual Joystick aur right side par A/B/X/Y buttons hain.\n" +
                        "- **Virtual Mouse**: Top bar par Mouse icon dabayein. Touch se cursor chalayein, single tap = Left Click, long press = Right Click.\n" +
                        "- **Virtual Keyboard**: Keyboard icon dabane se ESC, TAB, WASD aur arrows overlay khul jata hai!"
            }
            lower.contains("vulkan") || lower.contains("driver") || lower.contains("lag") || lower.contains("fps") -> {
                "Graphics aur Performance tips from Piko ⚡:\n" +
                        "- Game Details mein jakar 'SETTINGS' dabayein.\n" +
                        "- Agar device Snapdragon hai toh **Vulkan 1.3 (Turnip)** best FPS deta hai.\n" +
                        "- Mali GPU ke liye **OpenGL ES 3.2** stable rehta hai.\n" +
                        "- Top overlay mein aap live FPS aur RAM dekh sakte hain!"
            }
            else -> {
                "Assalam-o-Alaikum! Main hoon Piko, aapka XEMU AI Gaming Assistant! 🚀🤖\n\n" +
                        "Agar aap XEMU mein new hain aur kuch samajh nahi aa raha, toh aap mujhse pooch sakte hain:\n" +
                        "• 'Game kaisay import karein?'\n" +
                        "• 'Touch controls aur mouse kaisay kaam karte hain?'\n" +
                        "• 'Controller kaisay connect karein?'\n" +
                        "• 'Graphics settings aur FPS improve kaisay karein?'\n\n" +
                        "Bataiye, main aapki kya madad karoon?"
            }
        }
    }
}
