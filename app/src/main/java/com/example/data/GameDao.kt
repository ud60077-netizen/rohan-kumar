package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
  @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 30")
  fun getAllMatches(): Flow<List<MatchHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatch(match: MatchHistoryEntity): Long

  @Query("SELECT * FROM player_profile WHERE id = 1")
  fun getProfile(): Flow<PlayerProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveProfile(profile: PlayerProfileEntity)

  @Query("DELETE FROM match_history")
  suspend fun clearHistory()
}
