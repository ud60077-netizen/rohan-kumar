package com.example.model

data class Player(
  val id: Int,
  val name: String,
  val color: LudoColor,
  val isBot: Boolean = false,
  val avatarIndex: Int = 0,
  val pawns: List<Pawn> = listOf(
    Pawn(0, color),
    Pawn(1, color),
    Pawn(2, color),
    Pawn(3, color)
  ),
  val rank: Int = 0 // 0 = playing, 1 = 1st, 2 = 2nd, etc.
) {
  val isFinished: Boolean get() = pawns.all { it.isFinished }
  val finishedPawnsCount: Int get() = pawns.count { it.isFinished }
  val yardPawnsCount: Int get() = pawns.count { it.inYard }
  val activePawnsCount: Int get() = pawns.count { !it.inYard && !it.isFinished }

  fun getMovablePawnIds(diceRoll: Int): List<Int> {
    if (isFinished) return emptyList()
    return pawns.filter { it.canMove(diceRoll) }.map { it.id }
  }

  fun copyWithUpdatedPawn(pawnId: Int, newStep: Int): Player {
    val updatedPawns = pawns.map { pawn ->
      if (pawn.id == pawnId) pawn.copy(step = newStep) else pawn
    }
    return copy(pawns = updatedPawns)
  }
}
