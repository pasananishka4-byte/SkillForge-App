package com.skillforge.app.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import java.io.File

object SoundManager {

    private var soundPool: SoundPool? = null
    private var enabled = true
    private val soundIds = mutableMapOf<String, Int>()

    fun init(context: Context) {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attrs)
            .build()

        loadSounds(context)
    }

    private fun loadSounds(context: Context) {
        val names = listOf(
            Triple("tap", 440f, 0.12f),
            Triple("success", 660f, 0.15f),
            Triple("error", 220f, 0.25f),
            Triple("level_up", 880f, 0.3f),
            Triple("game_over", 330f, 0.4f),
            Triple("countdown", 550f, 0.1f)
        )

        names.forEach { (name, freq, dur) ->
            val file = generateWav(context, name, freq to dur)
            if (file != null) {
                val id = soundPool?.load(file.absolutePath, 1) ?: return@forEach
                soundIds[name] = id
            }
        }
    }

    private fun generateWav(context: Context, name: String, freqPair: Pair<Float, Float>): File? {
        val freq = freqPair.first
        val duration = freqPair.second
        return try {
            val file = File(context.cacheDir, "${name}.wav")
            if (file.exists()) return file

            val sampleRate = 22050
            val numSamples = (sampleRate * duration).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val envelope = if (i < numSamples / 8) {
                    i.toFloat() / (numSamples / 8)
                } else if (i > numSamples - numSamples / 8) {
                    (numSamples - i).toFloat() / (numSamples / 8)
                } else 1f
                val sample = (Math.sin(2.0 * Math.PI * freq * t) * Short.MAX_VALUE.toFloat() * 0.5 * envelope).toInt()
                samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val header = ByteArray(44)
            val dataSize = numSamples * 2
            header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
            writeInt(header, 4, 36 + dataSize)
            header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
            writeInt(header, 16, 16)
            writeShort(header, 20, 1)
            writeShort(header, 22, 1)
            writeInt(header, 24, sampleRate)
            writeInt(header, 28, sampleRate * 2)
            writeShort(header, 32, 2)
            writeShort(header, 34, 16)
            header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
            writeInt(header, 40, dataSize)

            val byteArray = ByteArray(44 + dataSize)
            header.copyInto(byteArray)
            for (i in samples.indices) {
                val idx = 44 + i * 2
                byteArray[idx] = (samples[i].toInt() and 0xFF).toByte()
                byteArray[idx + 1] = ((samples[i].toInt() shr 8) and 0xFF).toByte()
            }

            file.outputStream().use { it.write(byteArray) }
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun writeInt(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xFF).toByte()
        buffer[offset + 1] = ((value shr 8) and 0xFF).toByte()
        buffer[offset + 2] = ((value shr 16) and 0xFF).toByte()
        buffer[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun writeShort(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xFF).toByte()
        buffer[offset + 1] = ((value shr 8) and 0xFF).toByte()
    }

    fun play(name: String) {
        if (!enabled) return
        soundIds[name]?.let { id ->
            soundPool?.play(id, 0.5f, 0.5f, 1, 0, 1f)
        }
    }

    fun playTap() = play("tap")
    fun playSuccess() = play("success")
    fun playError() = play("error")
    fun playLevelUp() = play("level_up")
    fun playGameOver() = play("game_over")

    fun setEnabled(e: Boolean) { enabled = e }
    fun isEnabled() = enabled

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
