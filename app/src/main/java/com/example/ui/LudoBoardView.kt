package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.logic.GridCoord
import com.example.logic.LudoBoardCoordinates
import com.example.model.LudoColor
import com.example.model.LudoGameState
import com.example.model.Pawn
import com.example.model.Player
import com.example.ui.theme.LudoBoardCellBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.LudoGoldDark
import kotlin.math.hypot
import kotlin.math.min

@Composable
fun LudoBoardView(
  gameState: LudoGameState,
  onPawnClick: (pawnId: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val pulseAnim = remember { Animatable(1f) }
  LaunchedEffect(gameState.movablePawnIds) {
    if (gameState.movablePawnIds.isNotEmpty() && !gameState.isCurrentPlayerBot) {
      pulseAnim.animateTo(
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
          animation = tween(500, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      pulseAnim.snapTo(1f)
    }
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(6.dp)
      .shadow(16.dp, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .testTag("ludo_board")
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(gameState.movablePawnIds, gameState.currentTurnIndex, gameState.isCurrentPlayerBot) {
          if (gameState.isCurrentPlayerBot || gameState.movablePawnIds.isEmpty()) return@pointerInput

          detectTapGestures { tapOffset ->
            val cellSize = size.width / 15f
            val activePlayer = gameState.currentPlayer

            // Find closest movable pawn to tapOffset
            var closestPawnId: Int? = null
            var minDistance = Float.MAX_VALUE

            for (pawnId in gameState.movablePawnIds) {
              val pawn = activePlayer.pawns.firstOrNull { it.id == pawnId } ?: continue
              val coord = LudoBoardCoordinates.getPawnCoord(activePlayer.color, pawn.id, pawn.step)
              val pawnCenter = Offset(
                x = (coord.col + 0.5f) * cellSize,
                y = (coord.row + 0.5f) * cellSize
              )
              val dist = hypot(tapOffset.x - pawnCenter.x, tapOffset.y - pawnCenter.y)

              // If tapped on the pawn or near its cell (or yard area if pawn in yard)
              val threshold = if (pawn.inYard) cellSize * 2.5f else cellSize * 1.2f
              if (dist < threshold && dist < minDistance) {
                minDistance = dist
                closestPawnId = pawnId
              }
            }

            closestPawnId?.let { onPawnClick(it) }
          }
        }
    ) {
      val cellSize = size.width / 15f

      // 1. Draw Board Background & Gold Outer Rim
      drawRect(
        brush = Brush.linearGradient(
          colors = listOf(Color(0xFF281C4F), Color(0xFF160E30))
        ),
        size = size
      )

      // 2. Draw 4 Corner Yards (6x6 cells each)
      drawYard(LudoColor.RED, 0f, 0f, cellSize)
      drawYard(LudoColor.GREEN, 9f * cellSize, 0f, cellSize)
      drawYard(LudoColor.YELLOW, 9f * cellSize, 9f * cellSize, cellSize)
      drawYard(LudoColor.BLUE, 0f, 9f * cellSize, cellSize)

      // 3. Draw Track Grid cells (Arms)
      drawTrackArms(cellSize)

      // 4. Draw Center Home Square (3x3 cells, 6..8)
      drawCenterHome(cellSize)

      // 5. Draw All Player Pawns
      drawAllPawns(gameState, cellSize, pulseAnim.value)
    }
  }
}

private fun DrawScope.drawYard(
  color: LudoColor,
  left: Float,
  top: Float,
  cellSize: Float
) {
  val yardSize = 6f * cellSize

  // Solid Colored Quadrant Base
  drawRect(
    brush = Brush.radialGradient(
      colors = listOf(color.lightColor, color.primaryColor, color.darkColor),
      center = Offset(left + yardSize / 2f, top + yardSize / 2f),
      radius = yardSize * 0.7f
    ),
    topLeft = Offset(left, top),
    size = Size(yardSize, yardSize)
  )

  // Inner White Card
  val innerMargin = cellSize * 0.75f
  val innerSize = yardSize - 2f * innerMargin
  drawRoundRect(
    color = Color.White,
    topLeft = Offset(left + innerMargin, top + innerMargin),
    size = Size(innerSize, innerSize),
    cornerRadius = CornerRadius(cellSize * 0.4f, cellSize * 0.4f)
  )

  // Inner Card Border (Shadow)
  drawRoundRect(
    color = Color(0x33000000),
    topLeft = Offset(left + innerMargin, top + innerMargin),
    size = Size(innerSize, innerSize),
    cornerRadius = CornerRadius(cellSize * 0.4f, cellSize * 0.4f),
    style = Stroke(width = 1.5f)
  )

  // 4 Pawn Base Circles inside Yard
  val slots = when (color) {
    LudoColor.RED -> LudoBoardCoordinates.RED_YARD_SLOTS
    LudoColor.GREEN -> LudoBoardCoordinates.GREEN_YARD_SLOTS
    LudoColor.YELLOW -> LudoBoardCoordinates.YELLOW_YARD_SLOTS
    LudoColor.BLUE -> LudoBoardCoordinates.BLUE_YARD_SLOTS
  }

  for (slot in slots) {
    val center = Offset((slot.col + 0.5f) * cellSize, (slot.row + 0.5f) * cellSize)
    // Outer shadow ring
    drawCircle(
      color = Color(0x22000000),
      radius = cellSize * 0.48f,
      center = Offset(center.x, center.y + 2f)
    )
    // Circle base
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(color.lightColor, color.primaryColor),
        center = center,
        radius = cellSize * 0.45f
      ),
      radius = cellSize * 0.44f,
      center = center
    )
    // Inner white disc
    drawCircle(
      color = Color.White,
      radius = cellSize * 0.28f,
      center = center
    )
  }
}

private fun DrawScope.drawTrackArms(cellSize: Float) {
  val strokeColor = Color(0xFFCFD8DC)
  val cellBg = LudoBoardCellBg

  // Iterate over all 52 track cells + home stretch
  for (col in 0..14) {
    for (row in 0..14) {
      val isYard = (col < 6 && row < 6) || (col > 8 && row < 6) ||
        (col > 8 && row > 8) || (col < 6 && row > 8)
      val isCenter = (col in 6..8 && row in 6..8)

      if (!isYard && !isCenter) {
        val topLeft = Offset(col * cellSize, row * cellSize)
        val cellCenter = Offset(topLeft.x + cellSize / 2f, topLeft.y + cellSize / 2f)

        // Determine cell background color
        val fillColor = when {
          // Red Home corridor & Start
          row == 7 && col in 1..5 -> LudoColor.RED.primaryColor
          col == 1 && row == 6 -> LudoColor.RED.primaryColor

          // Green Home corridor & Start
          col == 7 && row in 1..5 -> LudoColor.GREEN.primaryColor
          col == 8 && row == 1 -> LudoColor.GREEN.primaryColor

          // Yellow Home corridor & Start
          row == 7 && col in 9..13 -> LudoColor.YELLOW.primaryColor
          col == 13 && row == 8 -> LudoColor.YELLOW.primaryColor

          // Blue Home corridor & Start
          col == 7 && row in 9..13 -> LudoColor.BLUE.primaryColor
          col == 6 && row == 13 -> LudoColor.BLUE.primaryColor

          // Normal track cell
          else -> cellBg
        }

        drawRect(
          color = fillColor,
          topLeft = topLeft,
          size = Size(cellSize, cellSize)
        )

        // Cell border
        drawRect(
          color = strokeColor,
          topLeft = topLeft,
          size = Size(cellSize, cellSize),
          style = Stroke(width = 1f)
        )

        // Safe Stars & Start Stars
        val isStar = (col == 1 && row == 6) || (col == 8 && row == 1) ||
          (col == 13 && row == 8) || (col == 6 && row == 13) ||
          (col == 6 && row == 2) || (col == 12 && row == 6) ||
          (col == 8 && row == 12) || (col == 2 && row == 8)

        if (isStar) {
          val isStartCell = (col == 1 && row == 6) || (col == 8 && row == 1) ||
            (col == 13 && row == 8) || (col == 6 && row == 13)
          drawStar(
            center = cellCenter,
            radius = cellSize * 0.32f,
            color = if (isStartCell) Color.White else Color(0xFFF57F17)
          )
        }
      }
    }
  }
}

private fun DrawScope.drawCenterHome(cellSize: Float) {
  val homeLeft = 6f * cellSize
  val homeTop = 6f * cellSize
  val homeSize = 3f * cellSize
  val center = Offset(homeLeft + homeSize / 2f, homeTop + homeSize / 2f)

  // 4 Triangles meeting at center
  // Red Triangle (Left)
  val redPath = Path().apply {
    moveTo(homeLeft, homeTop)
    lineTo(center.x, center.y)
    lineTo(homeLeft, homeTop + homeSize)
    close()
  }
  drawPath(redPath, LudoColor.RED.primaryColor)

  // Green Triangle (Top)
  val greenPath = Path().apply {
    moveTo(homeLeft, homeTop)
    lineTo(center.x, center.y)
    lineTo(homeLeft + homeSize, homeTop)
    close()
  }
  drawPath(greenPath, LudoColor.GREEN.primaryColor)

  // Yellow Triangle (Right)
  val yellowPath = Path().apply {
    moveTo(homeLeft + homeSize, homeTop)
    lineTo(center.x, center.y)
    lineTo(homeLeft + homeSize, homeTop + homeSize)
    close()
  }
  drawPath(yellowPath, LudoColor.YELLOW.primaryColor)

  // Blue Triangle (Bottom)
  val bluePath = Path().apply {
    moveTo(homeLeft, homeTop + homeSize)
    lineTo(center.x, center.y)
    lineTo(homeLeft + homeSize, homeTop + homeSize)
    close()
  }
  drawPath(bluePath, LudoColor.BLUE.primaryColor)

  // Dividing lines
  val dividerStroke = Stroke(width = 1.5f)
  drawPath(redPath, Color.White, style = dividerStroke)
  drawPath(greenPath, Color.White, style = dividerStroke)
  drawPath(yellowPath, Color.White, style = dividerStroke)
  drawPath(bluePath, Color.White, style = dividerStroke)

  // Center Royal Golden Medallion
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(LudoGold, LudoGoldDark),
      center = center,
      radius = cellSize * 0.85f
    ),
    radius = cellSize * 0.7f,
    center = center
  )
  drawCircle(
    color = Color.White,
    radius = cellSize * 0.7f,
    center = center,
    style = Stroke(width = 2f)
  )

  // Center Crown Drawing
  drawCrown(center, cellSize * 0.45f)
}

private fun DrawScope.drawCrown(center: Offset, size: Float) {
  val crownPath = Path().apply {
    val left = center.x - size * 0.8f
    val right = center.x + size * 0.8f
    val bottom = center.y + size * 0.5f
    val top = center.y - size * 0.5f

    moveTo(left, bottom)
    lineTo(left * 0.95f + right * 0.05f, top * 0.8f + bottom * 0.2f)
    lineTo(left * 0.6f + right * 0.4f, top * 0.3f + bottom * 0.7f)
    lineTo(center.x, top)
    lineTo(right * 0.6f + left * 0.4f, top * 0.3f + bottom * 0.7f)
    lineTo(right * 0.95f + left * 0.05f, top * 0.8f + bottom * 0.2f)
    lineTo(right, bottom)
    close()
  }
  drawPath(crownPath, Color(0xFFFFF9C4))
  drawPath(crownPath, Color(0xFFFF8F00), style = Stroke(width = 1.5f))
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
  val path = Path()
  val numPoints = 5
  val innerRadius = radius * 0.45f

  for (i in 0 until numPoints * 2) {
    val r = if (i % 2 == 0) radius else innerRadius
    val angle = (i * Math.PI / numPoints) - (Math.PI / 2.0)
    val x = center.x + (r * Math.cos(angle)).toFloat()
    val y = center.y + (r * Math.sin(angle)).toFloat()
    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
  }
  path.close()

  // Subtle shadow
  drawPath(
    path = path,
    color = Color(0x33000000),
    style = Stroke(width = 2f)
  )
  drawPath(path, color, style = Fill)
}

private fun DrawScope.drawAllPawns(
  gameState: LudoGameState,
  cellSize: Float,
  pulseFactor: Float
) {
  // Collect all pawns with their board positions
  data class PawnRenderItem(
    val player: Player,
    val pawn: Pawn,
    val center: Offset,
    val isMovable: Boolean
  )

  val renderItems = mutableListOf<PawnRenderItem>()
  val activePlayer = gameState.currentPlayer

  for (player in gameState.players) {
    for (pawn in player.pawns) {
      if (pawn.isFinished) continue // In Home

      val coord = LudoBoardCoordinates.getPawnCoord(player.color, pawn.id, pawn.step)
      val center = Offset((coord.col + 0.5f) * cellSize, (coord.row + 0.5f) * cellSize)
      val isMovable = (player.id == activePlayer.id) &&
        gameState.movablePawnIds.contains(pawn.id) &&
        !gameState.isCurrentPlayerBot

      renderItems.add(PawnRenderItem(player, pawn, center, isMovable))
    }
  }

  // Group by cell coordinate to offset overlapping pawns
  val cellBuckets = renderItems.groupBy { Pair((it.center.x / cellSize).toInt(), (it.center.y / cellSize).toInt()) }

  for ((_, items) in cellBuckets) {
    val count = items.size
    for (i in items.indices) {
      val item = items[i]
      var drawOffset = item.center

      // Apply offset if multiple pawns share cell (only on tracks)
      if (count > 1 && item.pawn.onTrack) {
        val spread = cellSize * 0.18f
        val angle = (i * 2.0 * Math.PI / count)
        drawOffset = Offset(
          x = item.center.x + (spread * Math.cos(angle)).toFloat(),
          y = item.center.y + (spread * Math.sin(angle)).toFloat()
        )
      }

      val scale = if (item.isMovable) pulseFactor else 1f
      drawPawn(
        center = drawOffset,
        color = item.player.color,
        cellSize = cellSize,
        scale = scale,
        isMovable = item.isMovable
      )
    }
  }
}

private fun DrawScope.drawPawn(
  center: Offset,
  color: LudoColor,
  cellSize: Float,
  scale: Float,
  isMovable: Boolean
) {
  val baseRadius = cellSize * 0.36f * scale
  val headRadius = cellSize * 0.22f * scale

  // If movable, draw pulsating golden halo
  if (isMovable) {
    drawCircle(
      color = LudoGold.copy(alpha = 0.5f),
      radius = baseRadius * 1.55f,
      center = center
    )
    drawCircle(
      color = Color.White.copy(alpha = 0.8f),
      radius = baseRadius * 1.35f,
      center = center,
      style = Stroke(width = 2.5f)
    )
  }

  // 1. Drop Shadow
  drawCircle(
    color = Color(0x55000000),
    radius = baseRadius * 1.05f,
    center = Offset(center.x, center.y + cellSize * 0.08f)
  )

  // 2. Outer Ring Base
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White, color.lightColor, color.darkColor),
      center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.2f),
      radius = baseRadius * 1.2f
    ),
    radius = baseRadius,
    center = center
  )

  // 3. Inner Colored Body Ring
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(color.lightColor, color.primaryColor, color.darkColor),
      center = Offset(center.x - baseRadius * 0.25f, center.y - baseRadius * 0.25f),
      radius = baseRadius
    ),
    radius = baseRadius * 0.78f,
    center = center
  )

  // 4. Pawn Head / Dome
  val headCenter = Offset(center.x, center.y - cellSize * 0.05f)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White, color.lightColor, color.darkColor),
      center = Offset(headCenter.x - headRadius * 0.3f, headCenter.y - headRadius * 0.35f),
      radius = headRadius * 1.3f
    ),
    radius = headRadius,
    center = headCenter
  )

  // 5. Specular Gloss Spot
  drawCircle(
    color = Color(0xCCFFFFFF),
    radius = headRadius * 0.32f,
    center = Offset(headCenter.x - headRadius * 0.3f, headCenter.y - headRadius * 0.35f)
  )
}
