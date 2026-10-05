package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.logic.LudoBoardCoordinates
import com.example.logic.LudoRuleEngine
import com.example.logic.SnakesAndLaddersEngine
import com.example.logic.SnakesGameState
import com.example.logic.SnakesPlayer
import com.example.model.GameMode
import com.example.model.LudoColor
import com.example.model.LudoGameState
import com.example.model.Pawn
import com.example.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameIsLudoKing() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ludo King", appName)
  }

  @Test
  fun testPawnCanMoveRules() {
    val redPawnInYard = Pawn(id = 0, color = LudoColor.RED, step = -1)
    // Needs 6 to unlock
    assertFalse(redPawnInYard.canMove(5))
    assertTrue(redPawnInYard.canMove(6))
    assertEquals(0, redPawnInYard.nextStep(6))

    // Active pawn on track
    val activePawn = Pawn(id = 1, color = LudoColor.RED, step = 10)
    assertTrue(activePawn.canMove(4))
    assertEquals(14, activePawn.nextStep(4))

    // Near home: step 54 needs <= 2
    val nearHomePawn = Pawn(id = 2, color = LudoColor.RED, step = 54)
    assertTrue(nearHomePawn.canMove(2))
    assertEquals(56, nearHomePawn.nextStep(2))
    assertFalse(nearHomePawn.canMove(3)) // Cannot overshoot!
  }

  @Test
  fun testSafeSquaresExist() {
    // Red, Green, Yellow, Blue start squares are safe
    assertTrue(LudoBoardCoordinates.isSafeSquare(0))
    assertTrue(LudoBoardCoordinates.isSafeSquare(13))
    assertTrue(LudoBoardCoordinates.isSafeSquare(26))
    assertTrue(LudoBoardCoordinates.isSafeSquare(39))
    // Intermediate star safe squares
    assertTrue(LudoBoardCoordinates.isSafeSquare(8))
    assertTrue(LudoBoardCoordinates.isSafeSquare(21))
    assertTrue(LudoBoardCoordinates.isSafeSquare(34))
    assertTrue(LudoBoardCoordinates.isSafeSquare(47))
  }

  @Test
  fun testSnakesAndLaddersMechanics() {
    val initialPlayer = SnakesPlayer(0, "Hero", LudoColor.RED, position = 1)
    val state = SnakesGameState(players = listOf(initialPlayer))

    // Rolling 3 from position 1 lands on 4, which is a LADDER to 14!
    val afterLadder = SnakesAndLaddersEngine.processTurn(state, 3)
    assertEquals(14, afterLadder.players.first().position)
    assertEquals("ladder", afterLadder.lastEncounter)

    // Moving to 17 (from pos 11 with roll 6) lands on snake which drops to 7
    val snakeState = SnakesGameState(players = listOf(initialPlayer.copy(position = 11)))
    val afterSnake = SnakesAndLaddersEngine.processTurn(snakeState, 6)
    assertEquals(7, afterSnake.players.first().position)
    assertEquals("snake", afterSnake.lastEncounter)
  }
}
