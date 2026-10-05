package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LudoColor
import com.example.ui.theme.LudoGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DiceView(
  diceValue: Int?,
  isRolling: Boolean,
  canRoll: Boolean,
  activeColor: LudoColor,
  size: Dp = 64.dp,
  onRollClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var displayFace by remember { mutableIntStateOf(diceValue ?: 6) }
  val rotation = remember { Animatable(0f) }
  val scale = remember { Animatable(1f) }

  // Animated glow for active player
  val pulseScale = remember { Animatable(1f) }
  LaunchedEffect(canRoll) {
    if (canRoll) {
      pulseScale.animateTo(
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
          animation = tween(600, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      pulseScale.snapTo(1f)
    }
  }

  // Rolling animation
  LaunchedEffect(isRolling) {
    if (isRolling) {
      // Rapid random faces during roll
      val rollJob = launch {
        while (isRolling) {
          displayFace = (1..6).random()
          delay(60)
        }
      }
      // Spin and bounce
      rotation.animateTo(
        targetValue = 360f,
        animationSpec = tween(durationMillis = 450, easing = LinearEasing)
      )
      rotation.snapTo(0f)
      rollJob.cancel()
      diceValue?.let { displayFace = it }
      // Landing squash and bounce
      scale.animateTo(0.85f, tween(80))
      scale.animateTo(1.1f, tween(100))
      scale.animateTo(1f, tween(100))
    } else if (diceValue != null) {
      displayFace = diceValue
    }
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(size + 16.dp)
      .scale(pulseScale.value)
  ) {
    // Outer glow ring when it's player's turn to roll
    if (canRoll) {
      Box(
        modifier = Modifier
          .size(size + 14.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.radialGradient(
              colors = listOf(activeColor.lightColor.copy(alpha = 0.6f), Color.Transparent)
            )
          )
      )
    }

    // The 3D Dice Box
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(size)
        .rotate(rotation.value)
        .scale(scale.value)
        .shadow(
          elevation = if (canRoll) 10.dp else 4.dp,
          shape = RoundedCornerShape(14.dp),
          ambientColor = activeColor.primaryColor,
          spotColor = activeColor.darkColor
        )
        .clip(RoundedCornerShape(14.dp))
        .background(
          Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFF0F0F5), Color(0xFFE2E4EB))
          )
        )
        .border(
          width = if (canRoll) 2.5.dp else 1.5.dp,
          brush = Brush.linearGradient(
            colors = if (canRoll) {
              listOf(LudoGold, activeColor.primaryColor)
            } else {
              listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
            }
          ),
          shape = RoundedCornerShape(14.dp)
        )
        .clickable(
          enabled = canRoll,
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onRollClick
        )
        .testTag("dice_button")
    ) {
      // Draw Pips (Dots)
      DicePips(
        face = displayFace,
        pipColor = activeColor.darkColor,
        modifier = Modifier.size(size - 12.dp)
      )
    }
  }
}

@Composable
fun DicePips(
  face: Int,
  pipColor: Color,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val pipRadius = size.width * 0.11f
    val left = size.width * 0.23f
    val center = size.width * 0.5f
    val right = size.width * 0.77f
    val top = size.height * 0.23f
    val middle = size.height * 0.5f
    val bottom = size.height * 0.77f

    fun drawPip(x: Float, y: Float) {
      // 3D Pip with shadow
      drawCircle(
        color = Color(0x33000000),
        radius = pipRadius * 1.15f,
        center = Offset(x, y + 1.5f)
      )
      drawCircle(
        color = pipColor,
        radius = pipRadius,
        center = Offset(x, y)
      )
      // Pip specular highlight
      drawCircle(
        color = Color(0x77FFFFFF),
        radius = pipRadius * 0.35f,
        center = Offset(x - pipRadius * 0.3f, y - pipRadius * 0.3f)
      )
    }

    when (face.coerceIn(1, 6)) {
      1 -> {
        drawPip(center, middle)
      }
      2 -> {
        drawPip(left, top)
        drawPip(right, bottom)
      }
      3 -> {
        drawPip(left, top)
        drawPip(center, middle)
        drawPip(right, bottom)
      }
      4 -> {
        drawPip(left, top)
        drawPip(right, top)
        drawPip(left, bottom)
        drawPip(right, bottom)
      }
      5 -> {
        drawPip(left, top)
        drawPip(right, top)
        drawPip(center, middle)
        drawPip(left, bottom)
        drawPip(right, bottom)
      }
      6 -> {
        drawPip(left, top)
        drawPip(right, top)
        drawPip(left, middle)
        drawPip(right, middle)
        drawPip(left, bottom)
        drawPip(right, bottom)
      }
    }
  }
}
