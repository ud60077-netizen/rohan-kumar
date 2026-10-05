package com.example.logic

import com.example.model.LudoGameState
import com.example.model.Pawn
import com.example.model.Player

object LudoBotAI {

  /**
   * Selects the best pawn ID to move for the bot player.
   */
  fun chooseBestPawn(
    gameState: LudoGameState,
    botPlayer: Player,
    diceRoll: Int
  ): Int? {
    val validPawnIds = botPlayer.getMovablePawnIds(diceRoll)
    if (validPawnIds.isEmpty()) return null
    if (validPawnIds.size == 1) return validPawnIds.first()

    var bestPawnId = validPawnIds.first()
    var bestScore = Int.MIN_VALUE

    for (pawnId in validPawnIds) {
      val pawn = botPlayer.pawns.first { it.id == pawnId }
      val score = evaluatePawnMove(gameState, botPlayer, pawn, diceRoll)
      if (score > bestScore) {
        bestScore = score
        bestPawnId = pawnId
      }
    }

    return bestPawnId
  }

  private fun evaluatePawnMove(
    gameState: LudoGameState,
    botPlayer: Player,
    pawn: Pawn,
    diceRoll: Int
  ): Int {
    var score = 10

    // Unlocking from yard
    if (pawn.inYard && diceRoll == 6) {
      // Prioritize unlocking if we have few active pawns
      score += 85
      return score
    }

    val newStep = pawn.nextStep(diceRoll)

    // Entering Home
    if (newStep == 56) {
      return 200 // Maximum priority to finish a pawn!
    }

    // Entering Home Stretch
    if (newStep in 51..55) {
      score += 100 + (newStep * 2)
    }

    // Checking main track actions
    if (newStep in 0..50) {
      val landingGlobalIdx = (botPlayer.color.startCellIndex + newStep) % 52
      val isSafe = LudoBoardCoordinates.isSafeSquare(landingGlobalIdx)

      // 1. Can we capture an opponent?
      if (!isSafe) {
        val opponentTokens = gameState.players
          .filter { it.id != botPlayer.id }
          .flatMap { it.pawns }
          .filter { it.onTrack && it.globalTrackPosition == landingGlobalIdx }

        if (opponentTokens.isNotEmpty()) {
          score += 180 // High priority to capture opponent!
        }
      } else {
        // Landing on safe square
        score += 50
      }

      // 2. Is this pawn currently in danger?
      val currentGlobalIdx = pawn.globalTrackPosition
      if (currentGlobalIdx != null && !LudoBoardCoordinates.isSafeSquare(currentGlobalIdx)) {
        val threats = gameState.players
          .filter { it.id != botPlayer.id }
          .flatMap { it.pawns }
          .filter { it.onTrack }
          .filter { oppPawn ->
            val oppPos = oppPawn.globalTrackPosition ?: -1
            val dist = (currentGlobalIdx - oppPos + 52) % 52
            dist in 1..6
          }

        if (threats.isNotEmpty()) {
          // Escaping danger is great!
          score += 65
        }
      }

      // 3. Advancing pawns forward
      score += newStep
    }

    return score
  }
}
