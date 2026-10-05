package com.example.model

data class Pawn(
  val id: Int,
  val color: LudoColor,
  val step: Int = -1 // -1: Yard, 0: Start, 1..50: Track, 51..55: Home Stretch, 56: Home Finished
) {
  val inYard: Boolean get() = step == -1
  val isFinished: Boolean get() = step == 56
  val inHomeStretch: Boolean get() = step in 51..55
  val onTrack: Boolean get() = step in 0..50

  fun canMove(diceRoll: Int): Boolean {
    if (isFinished) return false
    if (inYard) {
      return diceRoll == 6
    }
    return (step + diceRoll) <= 56
  }

  fun nextStep(diceRoll: Int): Int {
    if (inYard) {
      return if (diceRoll == 6) 0 else -1
    }
    return step + diceRoll
  }

  /**
   * Returns global 52-cell track position (0..51) if the pawn is on the main track,
   * or null if it's in yard, home stretch, or home.
   */
  val globalTrackPosition: Int?
    get() {
      if (step in 0..50) {
        return (color.startCellIndex + step) % 52
      }
      return null
    }
}
