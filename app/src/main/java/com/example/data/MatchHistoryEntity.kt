package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val gameMode: String,
  val winnerName: String,
  val winnerColor: String,
  val totalTurns: Int,
  val timestamp: Long = System.currentTimeMillis()
)
