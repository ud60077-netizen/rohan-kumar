package com.example.logic

import com.example.model.LudoColor

data class SnakesPlayer(
  val id: Int,
  val name: String,
  val color: LudoColor,
  val isBot: Boolean = false,
  val position: Int = 1, // 1 to 100
  val hasWon: Boolean = false
)

data class SnakesGameState(
  val players: List<SnakesPlayer> = emptyList(),
  val currentTurnIndex: Int = 0,
  val diceValue: Int? = null,
  val isRolling: Boolean = false,
  val message: String = "Roll the dice to climb to 100!",
  val winner: SnakesPlayer? = null,
  val lastEncounter: String? = null // "ladder" or "snake"
) {
  val currentPlayer: SnakesPlayer
    get() = if (players.isNotEmpty()) players[currentTurnIndex % players.size] else SnakesPlayer(0, "", LudoColor.RED)
}

object SnakesAndLaddersEngine {
  val LADDERS = mapOf(
    4 to 14,
    9 to 31,
    20 to 38,
    28 to 84,
    40 to 59,
    51 to 67,
    63 to 81,
    71 to 91
  )

  val SNAKES = mapOf(
    17 to 7,
    54 to 34,
    62 to 19,
    64 to 60,
    87 to 24,
    93 to 73,
    95 to 75,
    99 to 78
  )

  /**
   * Translates 1..100 to (col, row) on a 10x10 board.
   * col: 0..9 (0=left, 9=right)
   * row: 0..9 (0=bottom, 9=top)
   */
  fun getCoordinates(pos: Int): Pair<Int, Int> {
    val clamped = pos.coerceIn(1, 100)
    val row = (clamped - 1) / 10 // 0 to 9 from bottom
    val colInRow = (clamped - 1) % 10
    val col = if (row % 2 == 0) colInRow else (9 - colInRow)
    return Pair(col, row)
  }

  fun processTurn(
    state: SnakesGameState,
    diceRoll: Int
  ): SnakesGameState {
    val player = state.currentPlayer
    var newPos = player.position + diceRoll
    var encounterMsg: String? = null
    var logMsg = "${player.name} rolled $diceRoll"

    if (newPos > 100) {
      // Bounce back or stay
      val overshoot = newPos - 100
      newPos = 100 - overshoot
      logMsg += " and bounced back to $newPos"
    }

    if (LADDERS.containsKey(newPos)) {
      val ladderEnd = LADDERS[newPos]!!
      newPos = ladderEnd
      encounterMsg = "ladder"
      logMsg += " 🪜 CLIMBED A LADDER to $newPos!"
    } else if (SNAKES.containsKey(newPos)) {
      val snakeEnd = SNAKES[newPos]!!
      newPos = snakeEnd
      encounterMsg = "snake"
      logMsg += " 🐍 BITTEN BY A SNAKE! Slid down to $newPos..."
    }

    val won = (newPos == 100)
    val updatedPlayer = player.copy(position = newPos, hasWon = won)
    val updatedPlayers = state.players.map { if (it.id == player.id) updatedPlayer else it }

    val winner = if (won) updatedPlayer else null
    if (won) {
      logMsg = "🎉 ${player.name} REACHED 100 AND WON!"
    }

    // Next turn (extra roll if rolled 6 and didn't win)
    val nextIndex = if (diceRoll == 6 && !won) {
      state.currentTurnIndex // Extra turn!
    } else {
      (state.currentTurnIndex + 1) % state.players.size
    }

    return state.copy(
      players = updatedPlayers,
      currentTurnIndex = nextIndex,
      diceValue = diceRoll,
      message = logMsg,
      winner = winner,
      lastEncounter = encounterMsg
    )
  }
}
