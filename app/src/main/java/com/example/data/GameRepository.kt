package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(private val gameDao: GameDao) {
  val matchHistory: Flow<List<MatchHistoryEntity>> = gameDao.getAllMatches()
  val playerProfile: Flow<PlayerProfileEntity?> = gameDao.getProfile()

  suspend fun recordMatch(
    gameMode: String,
    winnerName: String,
    winnerColor: String,
    totalTurns: Int,
    isHumanWinner: Boolean,
    sixesRolled: Int = 0,
    pawnsCaptured: Int = 0
  ) {
    // 1. Insert match history
    gameDao.insertMatch(
      MatchHistoryEntity(
        gameMode = gameMode,
        winnerName = winnerName,
        winnerColor = winnerColor,
        totalTurns = totalTurns
      )
    )

    // 2. Update player profile stats
    val current = gameDao.getProfile().firstOrNull() ?: PlayerProfileEntity()
    val won = isHumanWinner
    val newStreak = if (won) current.currentStreak + 1 else 0
    val bestStreak = maxOf(current.bestStreak, newStreak)
    val coinsEarned = if (won) 500 else 100
    val diamondsEarned = if (won) 5 else 1

    val updatedProfile = current.copy(
      coins = current.coins + coinsEarned,
      diamonds = current.diamonds + diamondsEarned,
      matchesPlayed = current.matchesPlayed + 1,
      matchesWon = if (won) current.matchesWon + 1 else current.matchesWon,
      sixesRolled = current.sixesRolled + sixesRolled,
      pawnsCaptured = current.pawnsCaptured + pawnsCaptured,
      currentStreak = newStreak,
      bestStreak = bestStreak
    )
    gameDao.saveProfile(updatedProfile)
  }

  suspend fun savePlayerProfile(profile: PlayerProfileEntity) {
    gameDao.saveProfile(profile)
  }

  suspend fun clearHistory() {
    gameDao.clearHistory()
  }
}
