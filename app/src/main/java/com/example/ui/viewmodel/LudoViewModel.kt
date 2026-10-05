package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.MatchHistoryEntity
import com.example.data.PlayerProfileEntity
import com.example.logic.LudoBotAI
import com.example.logic.LudoRuleEngine
import com.example.logic.SnakesAndLaddersEngine
import com.example.logic.SnakesGameState
import com.example.logic.SnakesPlayer
import com.example.model.GameMode
import com.example.model.LudoColor
import com.example.model.LudoGameState
import com.example.model.Pawn
import com.example.model.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
  HOME,
  LUDO_GAME,
  SNAKES_GAME,
  STATS
}

class LudoViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  val repository = GameRepository(database.gameDao())
  val soundManager = SoundManager(application)

  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _ludoState = MutableStateFlow(LudoGameState())
  val ludoState: StateFlow<LudoGameState> = _ludoState.asStateFlow()

  private val _snakesState = MutableStateFlow(SnakesGameState())
  val snakesState: StateFlow<SnakesGameState> = _snakesState.asStateFlow()

  private val _isSoundEnabled = MutableStateFlow(true)
  val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

  private val _isHapticsEnabled = MutableStateFlow(true)
  val isHapticsEnabled: StateFlow<Boolean> = _isHapticsEnabled.asStateFlow()

  val matchHistory: StateFlow<List<MatchHistoryEntity>> = repository.matchHistory
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val playerProfile: StateFlow<PlayerProfileEntity?> = repository.playerProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private var botJob: Job? = null
  private var matchSixesCount = 0
  private var matchCapturesCount = 0

  fun navigateTo(screen: AppScreen) {
    botJob?.cancel()
    _currentScreen.value = screen
  }

  fun toggleSound() {
    _isSoundEnabled.value = !_isSoundEnabled.value
    soundManager.isSoundEnabled = _isSoundEnabled.value
  }

  fun toggleHaptics() {
    _isHapticsEnabled.value = !_isHapticsEnabled.value
    soundManager.isHapticsEnabled = _isHapticsEnabled.value
  }

  /**
   * Initializes and starts a new Ludo match.
   */
  fun startLudoGame(
    mode: GameMode,
    numPlayers: Int = 4,
    numBots: Int = 3,
    isQuick: Boolean = false
  ) {
    botJob?.cancel()
    matchSixesCount = 0
    matchCapturesCount = 0

    val tokenCount = if (isQuick) 2 else 4
    fun makePawns(color: LudoColor) = (0 until tokenCount).map { Pawn(it, color) }

    val players = when {
      mode == GameMode.VS_COMPUTER && numPlayers == 2 -> listOf(
        Player(0, "Player (You)", LudoColor.RED, isBot = false, pawns = makePawns(LudoColor.RED)),
        Player(1, "Bot Master", LudoColor.YELLOW, isBot = true, pawns = makePawns(LudoColor.YELLOW))
      )
      mode == GameMode.VS_COMPUTER -> listOf(
        Player(0, "Player (You)", LudoColor.RED, isBot = false, pawns = makePawns(LudoColor.RED)),
        Player(1, "Bot Leo", LudoColor.GREEN, isBot = true, pawns = makePawns(LudoColor.GREEN)),
        Player(2, "Bot King", LudoColor.YELLOW, isBot = true, pawns = makePawns(LudoColor.YELLOW)),
        Player(3, "Bot Maya", LudoColor.BLUE, isBot = true, pawns = makePawns(LudoColor.BLUE))
      )
      mode == GameMode.QUICK_LUDO -> listOf(
        Player(0, "Player 1", LudoColor.RED, isBot = false, pawns = makePawns(LudoColor.RED)),
        Player(1, "Bot Turbo", LudoColor.YELLOW, isBot = true, pawns = makePawns(LudoColor.YELLOW))
      )
      else -> {
        // Pass & Play (2 to 4 human players)
        val colors = listOf(LudoColor.RED, LudoColor.GREEN, LudoColor.YELLOW, LudoColor.BLUE)
        (0 until numPlayers).map { i ->
          Player(
            id = i,
            name = "Player ${i + 1}",
            color = colors[i],
            isBot = false,
            pawns = makePawns(colors[i])
          )
        }
      }
    }

    _ludoState.value = LudoGameState(
      mode = mode,
      players = players,
      currentTurnIndex = 0,
      diceValue = null,
      isRolling = false,
      hasRolled = false,
      consecutiveSixes = 0,
      movablePawnIds = emptyList(),
      turnMessage = "${players.first().name}'s turn to roll!",
      winnerRanks = emptyList(),
      isGameOver = false
    )

    _currentScreen.value = AppScreen.LUDO_GAME
  }

  /**
   * Rolls the dice for the current player.
   */
  fun rollDice() {
    val state = _ludoState.value
    if (state.isRolling || state.hasRolled || state.isGameOver) return

    viewModelScope.launch {
      _ludoState.value = state.copy(isRolling = true)
      soundManager.playDiceRoll()

      // Rolling duration
      delay(400)
      val roll = (1..6).random()

      val player = state.currentPlayer
      if (roll == 6) {
        matchSixesCount++
        soundManager.playSixRolled()
      }

      // Handle 3 consecutive sixes penalty
      val consecutive = if (roll == 6) state.consecutiveSixes + 1 else 0
      if (consecutive == 3) {
        // Penalty: skip turn
        _ludoState.value = state.copy(
          diceValue = roll,
          isRolling = false,
          hasRolled = true,
          consecutiveSixes = 0,
          movablePawnIds = emptyList(),
          turnMessage = "⚠️ 3 Sixes in a row! Turn skipped!"
        )
        delay(1200)
        advanceToNextPlayer()
        return@launch
      }

      val movable = player.getMovablePawnIds(roll)

      _ludoState.value = state.copy(
        diceValue = roll,
        isRolling = false,
        hasRolled = true,
        consecutiveSixes = consecutive,
        movablePawnIds = movable,
        turnMessage = when {
          movable.isEmpty() -> "${player.name} rolled $roll. No moves possible!"
          movable.size == 1 && !player.isBot -> "${player.name} rolled $roll. Tap token to move!"
          else -> "${player.name} rolled $roll. Choose a token!"
        }
      )

      if (movable.isEmpty()) {
        // Delay and pass to next player
        delay(1000)
        advanceToNextPlayer()
      } else if (player.isBot) {
        // Bot makes its move
        delay(600)
        val bestPawnId = LudoBotAI.chooseBestPawn(_ludoState.value, player, roll)
        if (bestPawnId != null) {
          movePawn(bestPawnId)
        } else {
          advanceToNextPlayer()
        }
      }
    }
  }

  /**
   * Moves a specific pawn for the current player.
   */
  fun movePawn(pawnId: Int) {
    val state = _ludoState.value
    val player = state.currentPlayer
    val dice = state.diceValue ?: return
    if (!state.movablePawnIds.contains(pawnId) || state.isGameOver) return

    val result = LudoRuleEngine.applyMove(state, player, pawnId, dice)

    // Play appropriate sound
    if (result.capturedPawn != null) {
      matchCapturesCount++
      soundManager.playPawnCapture()
    } else if (result.reachedHome || result.justFinishedGame) {
      soundManager.playVictory()
    } else {
      soundManager.playPawnStep()
    }

    val isOver = LudoRuleEngine.checkGameOver(result.updatedPlayers)

    _ludoState.value = state.copy(
      players = result.updatedPlayers,
      hasRolled = false,
      diceValue = null,
      movablePawnIds = emptyList(),
      turnMessage = result.logMessage,
      isGameOver = isOver
    )

    if (isOver) {
      // Record game in history & update player profile
      val winner = result.updatedPlayers.firstOrNull { it.rank == 1 } ?: player
      viewModelScope.launch {
        repository.recordMatch(
          gameMode = state.mode.title,
          winnerName = winner.name,
          winnerColor = winner.color.displayName,
          totalTurns = state.totalTurnsCount,
          isHumanWinner = !winner.isBot,
          sixesRolled = matchSixesCount,
          pawnsCaptured = matchCapturesCount
        )
      }
      return
    }

    // Check if player gets an extra roll
    if (result.awardsExtraRoll) {
      _ludoState.value = _ludoState.value.copy(
        turnMessage = "${player.name} gets a BONUS ROLL! 🎲"
      )
      if (player.isBot) {
        scheduleBotTurn()
      }
    } else {
      // Reset consecutive sixes and advance to next player
      _ludoState.value = _ludoState.value.copy(consecutiveSixes = 0)
      advanceToNextPlayer()
    }
  }

  private fun advanceToNextPlayer() {
    val state = _ludoState.value
    val nextIdx = LudoRuleEngine.getNextActiveTurnIndex(state.players, state.currentTurnIndex)
    val nextPlayer = state.players[nextIdx]

    _ludoState.value = state.copy(
      currentTurnIndex = nextIdx,
      diceValue = null,
      hasRolled = false,
      movablePawnIds = emptyList(),
      totalTurnsCount = state.totalTurnsCount + 1,
      turnMessage = "${nextPlayer.name}'s turn to roll!"
    )

    if (nextPlayer.isBot && !state.isGameOver) {
      scheduleBotTurn()
    }
  }

  private fun scheduleBotTurn() {
    botJob?.cancel()
    botJob = viewModelScope.launch {
      delay(700)
      if (!_ludoState.value.isGameOver && _ludoState.value.isCurrentPlayerBot) {
        rollDice()
      }
    }
  }

  // --- Snakes & Ladders Mode ---

  fun startSnakesGame(numPlayers: Int = 2, vsBot: Boolean = true) {
    val colors = listOf(LudoColor.RED, LudoColor.BLUE, LudoColor.GREEN, LudoColor.YELLOW)
    val players = if (vsBot) {
      listOf(
        SnakesPlayer(0, "Player (You)", LudoColor.RED, isBot = false),
        SnakesPlayer(1, "Bot Master", LudoColor.BLUE, isBot = true)
      )
    } else {
      (0 until numPlayers).map { i ->
        SnakesPlayer(i, "Player ${i + 1}", colors[i], isBot = false)
      }
    }

    _snakesState.value = SnakesGameState(
      players = players,
      currentTurnIndex = 0,
      diceValue = null,
      isRolling = false,
      message = "First to square 100 wins! Roll to start!",
      winner = null
    )
    _currentScreen.value = AppScreen.SNAKES_GAME
  }

  fun rollSnakesDice() {
    val state = _snakesState.value
    if (state.isRolling || state.winner != null) return

    viewModelScope.launch {
      _snakesState.value = state.copy(isRolling = true)
      soundManager.playDiceRoll()

      delay(400)
      val roll = (1..6).random()

      val updatedState = SnakesAndLaddersEngine.processTurn(state.copy(isRolling = false), roll)

      if (updatedState.lastEncounter == "ladder") {
        soundManager.playLadderClimb()
      } else if (updatedState.lastEncounter == "snake") {
        soundManager.playSnakeBite()
      } else {
        soundManager.playPawnStep()
      }

      if (updatedState.winner != null) {
        soundManager.playVictory()
        repository.recordMatch(
          gameMode = "Snakes & Ladders",
          winnerName = updatedState.winner.name,
          winnerColor = updatedState.winner.color.displayName,
          totalTurns = 0,
          isHumanWinner = !updatedState.winner.isBot
        )
      }

      _snakesState.value = updatedState

      // Check if next turn belongs to bot
      if (updatedState.winner == null && updatedState.currentPlayer.isBot) {
        delay(900)
        rollSnakesDice()
      }
    }
  }

  fun resetLudoGame() {
    val state = _ludoState.value
    startLudoGame(state.mode)
  }

  fun clearMatchHistory() {
    viewModelScope.launch {
      repository.clearHistory()
    }
  }
}
