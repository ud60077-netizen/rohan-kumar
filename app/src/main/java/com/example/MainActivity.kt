package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameMode
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LudoGameScreen
import com.example.ui.screens.SnakesLaddersScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.LudoRoyalBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LudoViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = LudoRoyalBg
        ) {
          LudoKingApp()
        }
      }
    }
  }
}

@Composable
fun LudoKingApp(
  viewModel: LudoViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val ludoState by viewModel.ludoState.collectAsStateWithLifecycle()
  val snakesState by viewModel.snakesState.collectAsStateWithLifecycle()
  val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
  val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsStateWithLifecycle()
  val profile by viewModel.playerProfile.collectAsStateWithLifecycle()
  val history by viewModel.matchHistory.collectAsStateWithLifecycle()

  when (currentScreen) {
    AppScreen.HOME -> {
      HomeScreen(
        profile = profile,
        isSoundEnabled = isSoundEnabled,
        isHapticsEnabled = isHapticsEnabled,
        onStartVsBot = { numPlayers ->
          viewModel.startLudoGame(GameMode.VS_COMPUTER, numPlayers = numPlayers, numBots = numPlayers - 1)
        },
        onStartPassPlay = { numPlayers ->
          viewModel.startLudoGame(GameMode.PASS_AND_PLAY, numPlayers = numPlayers, numBots = 0)
        },
        onStartQuickLudo = {
          viewModel.startLudoGame(GameMode.QUICK_LUDO, numPlayers = 2, numBots = 1, isQuick = true)
        },
        onStartSnakes = { numPlayers, vsBot ->
          viewModel.startSnakesGame(numPlayers = numPlayers, vsBot = vsBot)
        },
        onOpenStats = {
          viewModel.navigateTo(AppScreen.STATS)
        },
        onToggleSound = {
          viewModel.toggleSound()
        },
        onToggleHaptics = {
          viewModel.toggleHaptics()
        }
      )
    }

    AppScreen.LUDO_GAME -> {
      LudoGameScreen(
        gameState = ludoState,
        isSoundEnabled = isSoundEnabled,
        onRollDice = {
          viewModel.rollDice()
        },
        onPawnClick = { pawnId ->
          viewModel.movePawn(pawnId)
        },
        onToggleSound = {
          viewModel.toggleSound()
        },
        onRestartGame = {
          viewModel.resetLudoGame()
        },
        onQuitToHome = {
          viewModel.navigateTo(AppScreen.HOME)
        }
      )
    }

    AppScreen.SNAKES_GAME -> {
      SnakesLaddersScreen(
        gameState = snakesState,
        onRollDice = {
          viewModel.rollSnakesDice()
        },
        onRestart = {
          viewModel.startSnakesGame()
        },
        onBack = {
          viewModel.navigateTo(AppScreen.HOME)
        }
      )
    }

    AppScreen.STATS -> {
      StatsScreen(
        profile = profile,
        matchHistory = history,
        onBack = {
          viewModel.navigateTo(AppScreen.HOME)
        },
        onClearHistory = {
          viewModel.clearMatchHistory()
        }
      )
    }
  }
}
