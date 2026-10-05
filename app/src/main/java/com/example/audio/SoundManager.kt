package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {
  var isSoundEnabled: Boolean = true
  var isHapticsEnabled: Boolean = true

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private val scope = CoroutineScope(Dispatchers.Default)
  private val sampleRate = 22050

  fun playDiceRoll() {
    triggerHaptic(30)
    if (!isSoundEnabled) return
    scope.launch {
      // 3 rapid clicks for rolling rattle
      for (i in 0 until 4) {
        val tone = generateTone(500 + i * 80, 25, 0.5f)
        playBuffer(tone)
        kotlinx.coroutines.delay(35)
      }
    }
  }

  fun playPawnStep() {
    triggerHaptic(20)
    if (!isSoundEnabled) return
    scope.launch {
      val tone = generateTone(620, 40, 0.45f)
      playBuffer(tone)
    }
  }

  fun playSixRolled() {
    triggerHaptic(50)
    if (!isSoundEnabled) return
    scope.launch {
      // Ascending C - E - G - high C
      val freqs = listOf(523, 659, 784, 1046)
      for (f in freqs) {
        val tone = generateTone(f, 60, 0.6f)
        playBuffer(tone)
        kotlinx.coroutines.delay(40)
      }
    }
  }

  fun playPawnCapture() {
    triggerHaptic(80)
    if (!isSoundEnabled) return
    scope.launch {
      // Dramatic descending sweep
      val freqs = listOf(880, 660, 440, 220)
      for (f in freqs) {
        val tone = generateTone(f, 50, 0.7f)
        playBuffer(tone)
        kotlinx.coroutines.delay(30)
      }
    }
  }

  fun playVictory() {
    triggerHaptic(150)
    if (!isSoundEnabled) return
    scope.launch {
      // Royal fanfare
      val melody = listOf(
        Pair(523, 100), Pair(523, 100), Pair(523, 100),
        Pair(659, 200), Pair(784, 200), Pair(1046, 350)
      )
      for ((freq, duration) in melody) {
        val tone = generateTone(freq, duration, 0.7f)
        playBuffer(tone)
        kotlinx.coroutines.delay(duration.toLong() + 20)
      }
    }
  }

  fun playLadderClimb() {
    triggerHaptic(40)
    if (!isSoundEnabled) return
    scope.launch {
      val freqs = listOf(440, 554, 659, 880)
      for (f in freqs) {
        val tone = generateTone(f, 45, 0.5f)
        playBuffer(tone)
        kotlinx.coroutines.delay(30)
      }
    }
  }

  fun playSnakeBite() {
    triggerHaptic(70)
    if (!isSoundEnabled) return
    scope.launch {
      val freqs = listOf(600, 450, 300, 180)
      for (f in freqs) {
        val tone = generateTone(f, 60, 0.6f)
        playBuffer(tone)
        kotlinx.coroutines.delay(35)
      }
    }
  }

  private fun triggerHaptic(durationMs: Long) {
    if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(durationMs)
      }
    } catch (_: Exception) {
      // Ignore haptic errors on unsupported devices
    }
  }

  private fun generateTone(freqHz: Int, durationMs: Int, volume: Float): ShortArray {
    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
    val buffer = ShortArray(numSamples)
    for (i in 0 until numSamples) {
      val angle = 2.0 * PI * i / (sampleRate / freqHz.toDouble())
      // Envelope decay
      val decay = 1.0 - (i.toDouble() / numSamples)
      val sample = (sin(angle) * decay * volume * Short.MAX_VALUE).toInt()
      buffer[i] = sample.toShort()
    }
    return buffer
  }

  private fun playBuffer(buffer: ShortArray) {
    try {
      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
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

      track.write(buffer, 0, buffer.size)
      track.play()
      // Release track when done
      scope.launch {
        kotlinx.coroutines.delay(400)
        try {
          track.stop()
          track.release()
        } catch (_: Exception) {}
      }
    } catch (_: Exception) {}
  }
}
