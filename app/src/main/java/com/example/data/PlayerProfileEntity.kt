package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
  @PrimaryKey
  val id: Int = 1,
  val playerName: String = "Player 1",
  val coins: Int = 2500,
  val diamonds: Int = 50,
  val matchesPlayed: Int = 0,
  val matchesWon: Int = 0,
  val sixesRolled: Int = 0,
  val pawnsCaptured: Int = 0,
  val currentStreak: Int = 0,
  val bestStreak: Int = 0
)
