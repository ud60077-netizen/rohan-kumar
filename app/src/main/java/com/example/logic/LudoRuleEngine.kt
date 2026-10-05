package com.example.logic

import com.example.model.LudoColor
import com.example.model.LudoGameState
import com.example.model.Pawn
import com.example.model.Player

data class MoveResult(
  val updatedPlayers: List<Player>,
  val movedPawn: Pawn,
  val capturedPawn: Pawn? = null,
  val awardsExtraRoll: Boolean,
  val reachedHome: Boolean,
  val justFinishedGame: Boolean,
  val rankAwarded: Int = 0,
  val logMessage: String
)

object LudoRuleEngine {

  /**
   * Applies the pawn move and calculates captures, home entry, ranks, and extra turns.
   */
  fun applyMove(
    gameState: LudoGameState,
    movingPlayer: Player,
    pawnId: Int,
    diceRoll: Int
  ): MoveResult {
    val pawn = movingPlayer.pawns.first { it.id == pawnId }
    val newStep = pawn.nextStep(diceRoll)

    var capturedPawn: Pawn? = null
    var logMsg = "${movingPlayer.name} moved a token"

    if (pawn.inYard && newStep == 0) {
      logMsg = "${movingPlayer.name} unlocked a token to Start!"
    } else if (newStep == 56) {
      logMsg = "${movingPlayer.name}'s token reached HOME! 🏆"
    }

    // Check for captures on main track (step in 0..50)
    var updatedPlayers = gameState.players.toMutableList()

    if (newStep in 0..50) {
      val landingGlobalIdx = (movingPlayer.color.startCellIndex + newStep) % 52
      val isSafe = LudoBoardCoordinates.isSafeSquare(landingGlobalIdx)

      if (!isSafe) {
        // Look for opponent pawns on this cell
        for (i in updatedPlayers.indices) {
          val opponent = updatedPlayers[i]
          if (opponent.color != movingPlayer.color) {
            val opponentPawns = opponent.pawns.toMutableList()
            for (pIdx in opponentPawns.indices) {
              val oppPawn = opponentPawns[pIdx]
              if (oppPawn.onTrack && oppPawn.globalTrackPosition == landingGlobalIdx) {
                // Captured!
                capturedPawn = oppPawn
                opponentPawns[pIdx] = oppPawn.copy(step = -1)
                logMsg = "💥 ${movingPlayer.name} captured ${opponent.name}'s token!"
                break
              }
            }
            if (capturedPawn != null) {
              updatedPlayers[i] = opponent.copy(pawns = opponentPawns)
              break
            }
          }
        }
      }
    }

    // Update moving player's pawn
    val playerIdx = updatedPlayers.indexOfFirst { it.id == movingPlayer.id }
    val updatedPlayer = updatedPlayers[playerIdx].copyWithUpdatedPawn(pawnId, newStep)

    // Check if player just finished all tokens
    val reachedHome = (newStep == 56)
    var justFinished = false
    var rankAwarded = 0
    var finalPlayer = updatedPlayer

    if (updatedPlayer.isFinished && updatedPlayer.rank == 0) {
      val existingFinishedCount = updatedPlayers.count { it.isFinished && it.rank > 0 }
      rankAwarded = existingFinishedCount + 1
      justFinished = true
      finalPlayer = updatedPlayer.copy(rank = rankAwarded)
      logMsg = "👑 ${movingPlayer.name} finished in Rank $rankAwarded!"
    }

    updatedPlayers[playerIdx] = finalPlayer

    // Extra roll awarded if:
    // 1) Rolled a 6
    // 2) Captured an opponent pawn
    // 3) Pawn reached Home (step 56)
    val awardsExtra = (diceRoll == 6) || (capturedPawn != null) || reachedHome

    return MoveResult(
      updatedPlayers = updatedPlayers,
      movedPawn = pawn.copy(step = newStep),
      capturedPawn = capturedPawn,
      awardsExtraRoll = awardsExtra,
      reachedHome = reachedHome,
      justFinishedGame = justFinished,
      rankAwarded = rankAwarded,
      logMessage = logMsg
    )
  }

  /**
   * Advances the turn to the next active (unfinished) player.
   */
  fun getNextActiveTurnIndex(
    players: List<Player>,
    currentTurnIndex: Int
  ): Int {
    if (players.isEmpty()) return 0
    var nextIndex = (currentTurnIndex + 1) % players.size
    var loopCount = 0

    // Skip players who have finished all their tokens
    while (players[nextIndex].isFinished && loopCount < players.size) {
      nextIndex = (nextIndex + 1) % players.size
      loopCount++
    }

    return nextIndex
  }

  /**
   * Checks if the game has ended (only 1 player remains unfinished or all are finished).
   */
  fun checkGameOver(players: List<Player>): Boolean {
    val activePlayers = players.filter { !it.isFinished }
    return activePlayers.size <= 1
  }
}
