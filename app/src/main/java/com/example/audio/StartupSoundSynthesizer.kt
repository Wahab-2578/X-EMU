package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object StartupSoundSynthesizer {

    /**
     * Synthesizes and plays an original futuristic desktop-inspired electronic startup chime.
     * Harmonic pentatonic chord: D4, G4, A4, D5 with a warm shimmer harmonic envelope.
     */
    suspend fun playStartupChime() = withContext(Dispatchers.Default) {
        val sampleRate = 44100
        val durationSeconds = 2.2
        val numSamples = (durationSeconds * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        // Musical frequencies (Original sequence: D major suspended chord shimmer)
        // Note 1: D4 (starts at 0.0s)
        // Note 2: G4 (starts at 0.15s)
        // Note 3: A4 (starts at 0.30s)
        // Note 4: D5 (starts at 0.45s)
        // Note 5: F#5 (starts at 0.60s)
        val notes = listOf(
            Triple(293.66, 0.00, 1.8),
            Triple(392.00, 0.15, 1.7),
            Triple(440.00, 0.30, 1.6),
            Triple(587.33, 0.45, 1.5),
            Triple(739.99, 0.60, 1.4)
        )

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var sample = 0.0

            for ((freq, startTime, duration) in notes) {
                if (t >= startTime && t <= startTime + duration) {
                    val noteTime = t - startTime
                    // Attack (30ms linear ramp)
                    val attackTime = 0.03
                    val attack = if (noteTime < attackTime) (noteTime / attackTime) else 1.0
                    // Decay envelope (smooth exponential)
                    val decay = exp(-noteTime * 2.2)

                    // Fundamental sine + warm octave harmonic (0.25 amplitude)
                    val wave = sin(2.0 * PI * freq * noteTime) +
                            0.25 * sin(4.0 * PI * freq * noteTime) +
                            0.10 * sin(6.0 * PI * freq * noteTime)

                    sample += wave * attack * decay * 0.22
                }
            }

            // Master envelope fade out at very end
            val masterFade = if (t > durationSeconds - 0.2) {
                ((durationSeconds - t) / 0.2).coerceIn(0.0, 1.0)
            } else {
                1.0
            }

            val finalVal = (sample * masterFade).coerceIn(-1.0, 1.0)
            buffer[i] = (finalVal * 32767).toInt().toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release after playing duration
            kotlinx.coroutines.delay((durationSeconds * 1000).toLong())
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // AudioTrack playback safe fallback
        }
    }
}
