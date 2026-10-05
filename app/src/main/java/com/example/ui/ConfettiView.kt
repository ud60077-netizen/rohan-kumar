package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
  val x: Float,
  val initialY: Float,
  val speed: Float,
  val size: Float,
  val color: Color,
  val rotationSpeed: Float
)

@Composable
fun ConfettiView(modifier: Modifier = Modifier) {
  val animProgress = remember { Animatable(0f) }

  val particles = remember {
    val colors = listOf(
      Color(0xFFFFD700), Color(0xFFE53935), Color(0xFF2E7D32),
      Color(0xFF1565C0), Color(0xFFFBC02D), Color(0xFFE040FB),
      Color(0xFF00E5FF), Color(0xFFFFFFFF)
    )
    List(70) {
      ConfettiParticle(
        x = Random.nextFloat(),
        initialY = Random.nextFloat() * -0.5f,
        speed = 0.6f + Random.nextFloat() * 0.8f,
        size = 8f + Random.nextFloat() * 12f,
        color = colors.random(),
        rotationSpeed = Random.nextFloat() * 720f
      )
    }
  }

  LaunchedEffect(Unit) {
    animProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(3500, easing = LinearEasing)
    )
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    val progress = animProgress.value
    for (p in particles) {
      val curY = (p.initialY + progress * p.speed) * size.height
      val curX = (p.x * size.width) + kotlin.math.sin(progress * 10f + p.x * 20f) * 30f
      val rotation = progress * p.rotationSpeed

      if (curY in -50f..size.height + 50f) {
        rotate(rotation, Offset(curX, curY)) {
          drawRect(
            color = p.color,
            topLeft = Offset(curX - p.size / 2, curY - p.size / 2),
            size = Size(p.size, p.size * 0.6f)
          )
        }
      }
    }
  }
}
