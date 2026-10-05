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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logic.SnakesGameState
import com.example.ui.ConfettiView
import com.example.ui.DiceView
import com.example.ui.SnakesAndLaddersBoardView
import com.example.ui.theme.LudoCardBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.LudoRoyalBg
import com.example.ui.theme.LudoSurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakesLaddersScreen(
  gameState: SnakesGameState,
  onRollDice: () -> Unit,
  onRestart: () -> Unit,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  val canRoll = !gameState.isRolling && gameState.winner == null && !gameState.currentPlayer.isBot

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = "Snakes & Ladders",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("snakes_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          IconButton(onClick = onRestart) {
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
        // Status Banner
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
          Text(
            text = gameState.message,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
          )
        }

        // Players Position Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          for (p in gameState.players) {
            val isTurn = p.id == gameState.currentPlayer.id
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isTurn) p.color.primaryColor.copy(alpha = 0.25f) else LudoCardBg
              ),
              modifier = Modifier.padding(2.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(p.color.primaryColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = p.name, color = Color.White, fontSize = 12.sp, fontWeight = if (isTurn) FontWeight.Bold else FontWeight.Normal)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${p.position}/100", color = LudoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // 10x10 Board
        SnakesAndLaddersBoardView(
          gameState = gameState,
          modifier = Modifier.weight(1f, fill = false)
        )

        // Dice Roll Panel
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "${gameState.currentPlayer.name}'s Turn",
                color = gameState.currentPlayer.color.lightColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = if (gameState.currentPlayer.isBot) "Bot rolling..."
                else if (canRoll) "Tap dice to roll!"
                else "Waiting...",
                color = TextSecondary,
                fontSize = 12.sp
              )
            }

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

      // Victory Dialog
      AnimatedVisibility(
        visible = gameState.winner != null,
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

          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
            modifier = Modifier
              .fillMaxWidth(0.85f)
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
                Text(text = "🏆", fontSize = 40.sp)
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "CONGRATULATIONS!",
                color = LudoGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "${gameState.winner?.name} reached 100 first!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = LudoGold, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "Play Again", fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "Main Menu", color = Color.White)
              }
            }
          }
        }
      }
    }
  }
}
