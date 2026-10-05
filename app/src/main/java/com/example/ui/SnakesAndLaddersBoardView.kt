package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.logic.SnakesAndLaddersEngine
import com.example.logic.SnakesGameState
import com.example.logic.SnakesPlayer
import com.example.ui.theme.LudoGold
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SnakesAndLaddersBoardView(
  gameState: SnakesGameState,
  modifier: Modifier = Modifier
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(6.dp)
      .shadow(16.dp, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .testTag("snakes_ladders_board")
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val tileSize = size.width / 10f

      // 1. Draw 100 Squares
      val tileColors = listOf(
        Color(0xFFFFEBEE), Color(0xFFE8F5E9),
        Color(0xFFFFFDE7), Color(0xFFE3F2FD)
      )

      val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.DKGRAY
        textSize = tileSize * 0.28f
        isAntiAlias = true
        isFakeBoldText = true
      }

      for (num in 1..100) {
        val (col, row) = SnakesAndLaddersEngine.getCoordinates(num)
        // row: 0 is bottom, so Y is (9 - row) * tileSize
        val left = col * tileSize
        val top = (9 - row) * tileSize

        val colorIdx = (col + row) % tileColors.size
        drawRect(
          color = tileColors[colorIdx],
          topLeft = Offset(left, top),
          size = Size(tileSize, tileSize)
        )

        // Border
        drawRect(
          color = Color(0x33000000),
          topLeft = Offset(left, top),
          size = Size(tileSize, tileSize),
          style = Stroke(width = 0.8f)
        )

        // Draw tile number
        drawContext.canvas.nativeCanvas.drawText(
          num.toString(),
          left + tileSize * 0.12f,
          top + tileSize * 0.32f,
          textPaint
        )
      }

      // 2. Draw Ladders
      for ((start, end) in SnakesAndLaddersEngine.LADDERS) {
        val startCenter = getTileCenter(start, tileSize)
        val endCenter = getTileCenter(end, tileSize)
        drawLadder(startCenter, endCenter, tileSize * 0.28f)
      }

      // 3. Draw Snakes
      for ((head, tail) in SnakesAndLaddersEngine.SNAKES) {
        val headCenter = getTileCenter(head, tileSize)
        val tailCenter = getTileCenter(tail, tileSize)
        drawSnake(headCenter, tailCenter, tileSize * 0.25f)
      }

      // 4. Draw Player Tokens
      // Group players by position to offset
      val playersByPos = gameState.players.groupBy { it.position }
      for ((_, players) in playersByPos) {
        for (i in players.indices) {
          val p = players[i]
          val baseCenter = getTileCenter(p.position, tileSize)
          val offset = if (players.size > 1) {
            val angle = i * 2.0 * Math.PI / players.size
            Offset((tileSize * 0.2f * cos(angle)).toFloat(), (tileSize * 0.2f * sin(angle)).toFloat())
          } else Offset.Zero

          val tokenCenter = baseCenter + offset

          // Token drop shadow
          drawCircle(
            color = Color(0x55000000),
            radius = tileSize * 0.28f,
            center = Offset(tokenCenter.x, tokenCenter.y + 2f)
          )
          // Token outer body
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color.White, p.color.lightColor, p.color.darkColor),
              center = Offset(tokenCenter.x - tileSize * 0.08f, tokenCenter.y - tileSize * 0.08f),
              radius = tileSize * 0.3f
            ),
            radius = tileSize * 0.26f,
            center = tokenCenter
          )
          // Inner dot
          drawCircle(
            color = p.color.primaryColor,
            radius = tileSize * 0.14f,
            center = tokenCenter
          )
        }
      }
    }
  }
}

private fun getTileCenter(num: Int, tileSize: Float): Offset {
  val (col, row) = SnakesAndLaddersEngine.getCoordinates(num)
  val left = col * tileSize
  val top = (9 - row) * tileSize
  return Offset(left + tileSize / 2f, top + tileSize / 2f)
}

private fun DrawScope.drawLadder(start: Offset, end: Offset, width: Float) {
  val angle = atan2(end.y - start.y, end.x - start.x)
  val perpAngle = angle + (Math.PI / 2.0).toFloat()
  val halfW = width / 2f

  val dx = (halfW * cos(perpAngle)).toFloat()
  val dy = (halfW * sin(perpAngle)).toFloat()

  val railColor = Color(0xFF8D6E63)
  val rungColor = Color(0xFFD7CCC8)

  // Two rails
  drawLine(
    color = railColor,
    start = Offset(start.x - dx, start.y - dy),
    end = Offset(end.x - dx, end.y - dy),
    strokeWidth = 4f,
    cap = StrokeCap.Round
  )
  drawLine(
    color = railColor,
    start = Offset(start.x + dx, start.y + dy),
    end = Offset(end.x + dx, end.y + dy),
    strokeWidth = 4f,
    cap = StrokeCap.Round
  )

  // Rungs
  val distance = hypot(end.x - start.x, end.y - start.y)
  val numRungs = (distance / 24f).toInt().coerceAtLeast(3)
  for (i in 1..numRungs) {
    val t = i.toFloat() / (numRungs + 1)
    val rx = start.x + t * (end.x - start.x)
    val ry = start.y + t * (end.y - start.y)
    drawLine(
      color = rungColor,
      start = Offset(rx - dx, ry - dy),
      end = Offset(rx + dx, ry + dy),
      strokeWidth = 3f,
      cap = StrokeCap.Round
    )
  }
}

private fun DrawScope.drawSnake(head: Offset, tail: Offset, bodyThickness: Float) {
  val midX = (head.x + tail.x) / 2f + 25f
  val midY = (head.y + tail.y) / 2f - 25f

  val path = Path().apply {
    moveTo(head.x, head.y)
    quadraticBezierTo(midX, midY, tail.x, tail.y)
  }

  // Snake body
  drawPath(
    path = path,
    color = Color(0xFF2E7D32),
    style = Stroke(width = 8f, cap = StrokeCap.Round)
  )
  drawPath(
    path = path,
    color = Color(0xFF81C784),
    style = Stroke(width = 4f, cap = StrokeCap.Round)
  )

  // Snake Head
  drawCircle(
    color = Color(0xFF1B5E20),
    radius = bodyThickness * 0.7f,
    center = head
  )
  // Snake Eyes
  drawCircle(
    color = Color.Yellow,
    radius = 2.5f,
    center = Offset(head.x - 3f, head.y - 3f)
  )
  drawCircle(
    color = Color.Yellow,
    radius = 2.5f,
    center = Offset(head.x + 3f, head.y - 3f)
  )
}

private fun hypot(x: Float, y: Float): Float = kotlin.math.hypot(x, y)
