package com.example.logic

import androidx.compose.ui.geometry.Offset
import com.example.model.LudoColor

data class GridCoord(val col: Float, val row: Float)

object LudoBoardCoordinates {
  // Safe track positions on 52-cell track
  val SAFE_SQUARES = setOf(0, 8, 13, 21, 26, 34, 39, 47)

  // 52 cells of the outer track mapped to (col, row)
  val TRACK_COORDS: List<GridCoord> = listOf(
    // 0..4 (Red Start heading toward top arm)
    GridCoord(1f, 6f),  // 0 - RED START (Safe)
    GridCoord(2f, 6f),  // 1
    GridCoord(3f, 6f),  // 2
    GridCoord(4f, 6f),  // 3
    GridCoord(5f, 6f),  // 4

    // 5..10 (Top arm, going UP left side)
    GridCoord(6f, 5f),  // 5
    GridCoord(6f, 4f),  // 6
    GridCoord(6f, 3f),  // 7
    GridCoord(6f, 2f),  // 8 - SAFE STAR
    GridCoord(6f, 1f),  // 9
    GridCoord(6f, 0f),  // 10

    // 11..12 (Top bridge)
    GridCoord(7f, 0f),  // 11
    GridCoord(8f, 0f),  // 12

    // 13..17 (Top arm, going DOWN right side)
    GridCoord(8f, 1f),  // 13 - GREEN START (Safe)
    GridCoord(8f, 2f),  // 14
    GridCoord(8f, 3f),  // 15
    GridCoord(8f, 4f),  // 16
    GridCoord(8f, 5f),  // 17

    // 18..23 (Right arm, going RIGHT top side)
    GridCoord(9f, 6f),  // 18
    GridCoord(10f, 6f), // 19
    GridCoord(11f, 6f), // 20
    GridCoord(12f, 6f), // 21 - SAFE STAR
    GridCoord(13f, 6f), // 22
    GridCoord(14f, 6f), // 23

    // 24..25 (Right bridge)
    GridCoord(14f, 7f), // 24
    GridCoord(14f, 8f), // 25

    // 26..30 (Right arm, going LEFT bottom side)
    GridCoord(13f, 8f), // 26 - YELLOW START (Safe)
    GridCoord(12f, 8f), // 27
    GridCoord(11f, 8f), // 28
    GridCoord(10f, 8f), // 29
    GridCoord(9f, 8f),  // 30

    // 31..36 (Bottom arm, going DOWN right side)
    GridCoord(8f, 9f),  // 31
    GridCoord(8f, 10f), // 32
    GridCoord(8f, 11f), // 33
    GridCoord(8f, 12f), // 34 - SAFE STAR
    GridCoord(8f, 13f), // 35
    GridCoord(8f, 14f), // 36

    // 37..38 (Bottom bridge)
    GridCoord(7f, 14f), // 37
    GridCoord(6f, 14f), // 38

    // 39..43 (Bottom arm, going UP left side)
    GridCoord(6f, 13f), // 39 - BLUE START (Safe)
    GridCoord(6f, 12f), // 40
    GridCoord(6f, 11f), // 41
    GridCoord(6f, 10f), // 42
    GridCoord(6f, 9f),  // 43

    // 44..49 (Left arm, going LEFT bottom side)
    GridCoord(5f, 8f),  // 44
    GridCoord(4f, 8f),  // 45
    GridCoord(3f, 8f),  // 46
    GridCoord(2f, 8f),  // 47 - SAFE STAR
    GridCoord(1f, 8f),  // 48
    GridCoord(0f, 8f),  // 49

    // 50..51 (Left bridge)
    GridCoord(0f, 7f),  // 50
    GridCoord(0f, 6f)   // 51
  )

  // Home stretch coordinates (step 51..55) and Center Home (step 56)
  val RED_HOME_STRETCH = listOf(
    GridCoord(1f, 7f),  // 51
    GridCoord(2f, 7f),  // 52
    GridCoord(3f, 7f),  // 53
    GridCoord(4f, 7f),  // 54
    GridCoord(5f, 7f),  // 55
    GridCoord(6.3f, 7f) // 56 (Home triangle)
  )

  val GREEN_HOME_STRETCH = listOf(
    GridCoord(7f, 1f),  // 51
    GridCoord(7f, 2f),  // 52
    GridCoord(7f, 3f),  // 53
    GridCoord(7f, 4f),  // 54
    GridCoord(7f, 5f),  // 55
    GridCoord(7f, 6.3f) // 56 (Home triangle)
  )

  val YELLOW_HOME_STRETCH = listOf(
    GridCoord(13f, 7f), // 51
    GridCoord(12f, 7f), // 52
    GridCoord(11f, 7f), // 53
    GridCoord(10f, 7f), // 54
    GridCoord(9f, 7f),  // 55
    GridCoord(7.7f, 7f) // 56 (Home triangle)
  )

  val BLUE_HOME_STRETCH = listOf(
    GridCoord(7f, 13f), // 51
    GridCoord(7f, 12f), // 52
    GridCoord(7f, 11f), // 53
    GridCoord(7f, 10f), // 54
    GridCoord(7f, 9f),  // 55
    GridCoord(7f, 7.7f) // 56 (Home triangle)
  )

  // 4 Base Yard pawn slots
  val RED_YARD_SLOTS = listOf(
    GridCoord(1.5f, 1.5f),
    GridCoord(3.5f, 1.5f),
    GridCoord(1.5f, 3.5f),
    GridCoord(3.5f, 3.5f)
  )

  val GREEN_YARD_SLOTS = listOf(
    GridCoord(10.5f, 1.5f),
    GridCoord(12.5f, 1.5f),
    GridCoord(10.5f, 3.5f),
    GridCoord(12.5f, 3.5f)
  )

  val YELLOW_YARD_SLOTS = listOf(
    GridCoord(10.5f, 10.5f),
    GridCoord(12.5f, 10.5f),
    GridCoord(10.5f, 12.5f),
    GridCoord(12.5f, 12.5f)
  )

  val BLUE_YARD_SLOTS = listOf(
    GridCoord(1.5f, 10.5f),
    GridCoord(3.5f, 10.5f),
    GridCoord(1.5f, 12.5f),
    GridCoord(3.5f, 12.5f)
  )

  /**
   * Resolves a pawn's current grid coordinates on the 15x15 board.
   */
  fun getPawnCoord(color: LudoColor, pawnId: Int, step: Int): GridCoord {
    if (step == -1) {
      val slots = when (color) {
        LudoColor.RED -> RED_YARD_SLOTS
        LudoColor.GREEN -> GREEN_YARD_SLOTS
        LudoColor.YELLOW -> YELLOW_YARD_SLOTS
        LudoColor.BLUE -> BLUE_YARD_SLOTS
      }
      return slots[pawnId % slots.size]
    }

    if (step in 0..50) {
      val globalIdx = (color.startCellIndex + step) % 52
      return TRACK_COORDS[globalIdx]
    }

    // Home stretch 51..56
    val stretchIdx = (step - 51).coerceIn(0, 5)
    return when (color) {
      LudoColor.RED -> RED_HOME_STRETCH[stretchIdx]
      LudoColor.GREEN -> GREEN_HOME_STRETCH[stretchIdx]
      LudoColor.YELLOW -> YELLOW_HOME_STRETCH[stretchIdx]
      LudoColor.BLUE -> BLUE_HOME_STRETCH[stretchIdx]
    }
  }

  fun isSafeSquare(globalTrackIndex: Int): Boolean {
    return SAFE_SQUARES.contains(globalTrackIndex)
  }
}
