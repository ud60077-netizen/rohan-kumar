package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LudoGameState
import com.example.ui.ConfettiView
import com.example.ui.DiceView
import com.example.ui.LudoBoardView
import com.example.ui.PlayerCardView
import com.example.ui.theme.LudoCardBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.LudoRoyalBg
import com.example.ui.theme.LudoSurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LudoGameScreen(
  gameState: LudoGameState,
  isSoundEnabled: Boolean,
  onRollDice: () -> Unit,
  onPawnClick: (pawnId: Int) -> Unit,
  onToggleSound: () -> Unit,
  onRestartGame: () -> Unit,
  onQuitToHome: () -> Unit
) {
  var showQuitDialog by remember { mutableStateOf(false) }

  BackHandler {
    showQuitDialog = true
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = gameState.mode.title,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "Turn ${gameState.totalTurnsCount + 1}",
              color = TextMuted,
              fontSize = 11.sp
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = { showQuitDialog = true }, modifier = Modifier.testTag("game_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quit", tint = Color.White)
          }
        },
        actions = {
          IconButton(onClick = onToggleSound) {
            Icon(
              imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
              contentDescription = "Sound Toggle",
              tint = if (isSoundEnabled) LudoGold else TextMuted
            )
          }
          IconButton(onClick = onRestartGame) {
            Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LudoRoyalBg)
      )
    },
    containerColor = LudoRoyalBg
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // 1. Top Turn Banner Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            // Active player color bullet
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(gameState.currentPlayer.color.primaryColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = gameState.turnMessage,
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        // 2. Top Player Cards (Red & Green)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val redPlayer = gameState.players.getOrNull(0)
          val greenPlayer = gameState.players.getOrNull(1)

          if (redPlayer != null) {
            PlayerCardView(
              player = redPlayer,
              isCurrentTurn = gameState.currentPlayer.id == redPlayer.id,
              modifier = Modifier.weight(1f)
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }

          if (greenPlayer != null) {
            PlayerCardView(
              player = greenPlayer,
              isCurrentTurn = gameState.currentPlayer.id == greenPlayer.id,
              modifier = Modifier.weight(1f)
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
        }

        // 3. Central 15x15 Ludo Board
        LudoBoardView(
          gameState = gameState,
          onPawnClick = onPawnClick,
          modifier = Modifier.weight(1f, fill = false)
        )

        // 4. Bottom Player Cards (Blue & Yellow)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val bluePlayer = gameState.players.firstOrNull { it.color.name == "BLUE" }
          val yellowPlayer = gameState.players.firstOrNull { it.color.name == "YELLOW" }

          if (bluePlayer != null) {
            PlayerCardView(
              player = bluePlayer,
              isCurrentTurn = gameState.currentPlayer.id == bluePlayer.id,
              modifier = Modifier.weight(1f)
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }

          if (yellowPlayer != null) {
            PlayerCardView(
              player = yellowPlayer,
              isCurrentTurn = gameState.currentPlayer.id == yellowPlayer.id,
              modifier = Modifier.weight(1f)
            )
          } else {
            Spacer(modifier = Modifier.weight(1f))
          }
        }

        // 5. Bottom Dice Controls Panel
        val canRoll = !gameState.hasRolled && !gameState.isRolling && !gameState.isCurrentPlayerBot && !gameState.isGameOver

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Player info
            Column {
              Text(
                text = "${gameState.currentPlayer.name}'s Turn",
                color = gameState.currentPlayer.color.lightColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = if (gameState.isCurrentPlayerBot) "Computer thinking..."
                else if (canRoll) "Tap dice to roll!"
                else if (gameState.movablePawnIds.isNotEmpty()) "Select a token to move"
                else "Waiting...",
                color = TextSecondary,
                fontSize = 12.sp
              )
            }

            // Dice
            DiceView(
              diceValue = gameState.diceValue,
              isRolling = gameState.isRolling,
              canRoll = canRoll,
              activeColor = gameState.currentPlayer.color,
              onRollClick = onRollDice
            )
          }
        }
      }

      // Victory Dialog & Confetti Celebration
      AnimatedVisibility(
        visible = gameState.isGameOver,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
          contentAlignment = Alignment.Center
        ) {
          ConfettiView()

          val winner = gameState.players.firstOrNull { it.rank == 1 } ?: gameState.currentPlayer

          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
            modifier = Modifier
              .fillMaxWidth(0.88f)
              .padding(16.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
                  .background(LudoGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "👑", fontSize = 42.sp)
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "MATCH FINISHED!",
                color = LudoGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "${winner.name} is the King! 🏆",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Rankings table
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(LudoCardBg)
                  .padding(12.dp)
              ) {
                val sortedPlayers = gameState.players.sortedBy { if (it.rank > 0) it.rank else 99 }
                for (p in sortedPlayers) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(10.dp)
                          .clip(CircleShape)
                          .background(p.color.primaryColor)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(text = p.name, color = Color.White, fontSize = 13.sp)
                    }
                    Text(
                      text = if (p.rank > 0) "Rank #${p.rank}" else "DNF",
                      color = if (p.rank == 1) LudoGold else TextSecondary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = onRestartGame,
                colors = ButtonDefaults.buttonColors(containerColor = LudoGold, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "Play Again", fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedButton(
                onClick = onQuitToHome,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "Main Menu", color = Color.White)
              }
            }
          }
        }
      }

      // Quit Confirmation Dialog
      if (showQuitDialog) {
        AlertDialog(
          onDismissRequest = { showQuitDialog = false },
          title = { Text(text = "Leave Game?", color = Color.White, fontWeight = FontWeight.Bold) },
          text = { Text(text = "Current match progress will be lost.", color = TextSecondary) },
          confirmButton = {
            Button(
              onClick = {
                showQuitDialog = false
                onQuitToHome()
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
              Text("Leave")
            }
          },
          dismissButton = {
            TextButton(onClick = { showQuitDialog = false }) {
              Text("Continue Playing", color = LudoGold)
            }
          },
          containerColor = LudoSurfaceBg
        )
      }
    }
  }
}
