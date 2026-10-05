package com.example.model

data class LudoGameState(
  val mode: GameMode = GameMode.VS_COMPUTER,
  val players: List<Player> = emptyList(),
  val currentTurnIndex: Int = 0,
  val diceValue: Int? = null,
  val isRolling: Boolean = false,
  val hasRolled: Boolean = false,
  val consecutiveSixes: Int = 0,
  val movablePawnIds: List<Int> = emptyList(),
  val turnMessage: String = "Tap the dice to roll!",
  val winnerRanks: List<Player> = emptyList(),
  val isGameOver: Boolean = false,
  val totalTurnsCount: Int = 0,
  val lastCaptureEvent: String? = null
) {
  val currentPlayer: Player
    get() = if (players.isNotEmpty()) players[currentTurnIndex % players.size] else Player(0, "", LudoColor.RED)

  val isCurrentPlayerBot: Boolean
    get() = currentPlayer.isBot
}
