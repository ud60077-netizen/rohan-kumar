package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProfileEntity
import com.example.model.GameMode
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.LudoCardBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.LudoGoldDark
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoRoyalBg
import com.example.ui.theme.LudoSurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  profile: PlayerProfileEntity?,
  isSoundEnabled: Boolean,
  isHapticsEnabled: Boolean,
  onStartVsBot: (numPlayers: Int) -> Unit,
  onStartPassPlay: (numPlayers: Int) -> Unit,
  onStartQuickLudo: () -> Unit,
  onStartSnakes: (numPlayers: Int, vsBot: Boolean) -> Unit,
  onOpenStats: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleHaptics: () -> Unit
) {
  var showRulesDialog by remember { mutableStateOf(false) }
  var setupMode by remember { mutableStateOf<GameMode?>(null) }
  var selectedPlayerCount by remember { mutableIntStateOf(4) }
  var snakesVsBot by remember { mutableStateOf(true) }

  val p = profile ?: PlayerProfileEntity()

  Scaffold(
    containerColor = LudoRoyalBg
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Top Bar: Currency + Controls
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Coins & Gems Pills
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(LudoSurfaceBg)
              .border(1.dp, LudoGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🪙", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${p.coins}",
                color = LudoGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(LudoSurfaceBg)
              .border(1.dp, AccentCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "💎", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${p.diamonds}",
                color = AccentCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }

        // Action Icons
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(onClick = onToggleSound) {
            Icon(
              imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
              contentDescription = "Sound",
              tint = if (isSoundEnabled) LudoGold else TextMuted
            )
          }
          IconButton(onClick = onToggleHaptics) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = "Haptics",
              tint = if (isHapticsEnabled) LudoGold else TextMuted
            )
          }
          IconButton(onClick = { showRulesDialog = true }) {
            Icon(Icons.Default.HelpOutline, contentDescription = "Rules", tint = Color.White)
          }
          IconButton(onClick = onOpenStats, modifier = Modifier.testTag("stats_button")) {
            Icon(Icons.Default.BarChart, contentDescription = "Stats", tint = Color.White)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Royal Hero Banner
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
        modifier = Modifier
          .fillMaxWidth()
          .shadow(8.dp, RoundedCornerShape(20.dp))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFF381A64),
                  Color(0xFF1E133D),
                  Color(0xFF100A24)
                )
              )
            )
            .border(
              width = 1.5.dp,
              brush = Brush.linearGradient(listOf(LudoGold, Color(0x33FFD700))),
              shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👑", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "LUDO KING",
                  color = LudoGold,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 24.sp,
                  letterSpacing = 1.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "The Classic Royal Board Game",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(10.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                QuadrantDot(LudoRed)
                QuadrantDot(LudoGreen)
                QuadrantDot(LudoGold)
                QuadrantDot(Color(0xFF1565C0))
              }
            }

            // Big 3D Dice Graphic
            Box(
              modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.linearGradient(
                    listOf(Color.White, Color(0xFFE2E8F0))
                  )
                )
                .border(2.dp, LudoGold, RoundedCornerShape(14.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🎲", fontSize = 36.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SELECT GAME MODE",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          letterSpacing = 1.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Game Mode Cards
      GameModeCard(
        title = "Play Vs Computer",
        subtitle = "Challenge offline AI Bots in 2 or 4 player match",
        icon = Icons.Default.SmartToy,
        gradient = listOf(Color(0xFF0D47A1), Color(0xFF1565C0)),
        badge = "SMART BOT",
        testTag = "mode_vs_computer",
        onClick = {
          setupMode = GameMode.VS_COMPUTER
          selectedPlayerCount = 4
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      GameModeCard(
        title = "Pass & Play",
        subtitle = "Local multiplayer with 2, 3, or 4 friends",
        icon = Icons.Default.Group,
        gradient = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)),
        badge = "MULTIPLAYER",
        testTag = "mode_pass_play",
        onClick = {
          setupMode = GameMode.PASS_AND_PLAY
          selectedPlayerCount = 4
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      GameModeCard(
        title = "Quick Ludo",
        subtitle = "Fast-paced match with 2 tokens per player",
        icon = Icons.Default.Bolt,
        gradient = listOf(Color(0xFFB71C1C), Color(0xFFE53935)),
        badge = "FAST RUSH",
        testTag = "mode_quick_ludo",
        onClick = onStartQuickLudo
      )

      Spacer(modifier = Modifier.height(10.dp))

      GameModeCard(
        title = "Snakes & Ladders",
        subtitle = "Classic 100-cell sister game with ladders & snakes",
        icon = Icons.Default.Casino,
        gradient = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2)),
        badge = "CLASSIC MINI",
        testTag = "mode_snakes_ladders",
        onClick = {
          setupMode = GameMode.SNAKES_AND_LADDERS
          selectedPlayerCount = 2
          snakesVsBot = true
        }
      )

      Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal Sheet for Match Configuration
    if (setupMode != null) {
      val mode = setupMode!!
      val sheetState = rememberModalBottomSheetState()
      val scope = rememberCoroutineScope()

      ModalBottomSheet(
        onDismissRequest = { setupMode = null },
        sheetState = sheetState,
        containerColor = LudoSurfaceBg,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = mode.title,
            color = LudoGold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Configure match settings",
            color = TextSecondary,
            fontSize = 13.sp
          )

          Spacer(modifier = Modifier.height(20.dp))

          if (mode == GameMode.SNAKES_AND_LADDERS) {
            Text(
              text = "Choose Opponent:",
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              FilterChip(
                selected = snakesVsBot,
                onClick = { snakesVsBot = true },
                label = { Text("Vs Computer Bot") },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = LudoGold,
                  selectedLabelColor = Color.Black
                ),
                modifier = Modifier.weight(1f)
              )
              FilterChip(
                selected = !snakesVsBot,
                onClick = { snakesVsBot = false },
                label = { Text("2 Players Local") },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = LudoGold,
                  selectedLabelColor = Color.Black
                ),
                modifier = Modifier.weight(1f)
              )
            }
          } else {
            // Ludo Player Count Selection
            Text(
              text = "Number of Players:",
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              val counts = if (mode == GameMode.VS_COMPUTER) listOf(2, 4) else listOf(2, 3, 4)
              for (count in counts) {
                FilterChip(
                  selected = selectedPlayerCount == count,
                  onClick = { selectedPlayerCount = count },
                  label = {
                    Text(
                      text = "$count Players",
                      fontWeight = if (selectedPlayerCount == count) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LudoGold,
                    selectedLabelColor = Color.Black,
                    containerColor = LudoCardBg,
                    labelColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = {
              scope.launch { sheetState.hide() }.invokeOnCompletion {
                val currentMode = setupMode
                setupMode = null
                when (currentMode) {
                  GameMode.VS_COMPUTER -> onStartVsBot(selectedPlayerCount)
                  GameMode.PASS_AND_PLAY -> onStartPassPlay(selectedPlayerCount)
                  GameMode.SNAKES_AND_LADDERS -> onStartSnakes(2, snakesVsBot)
                  else -> {}
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = LudoGold, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("start_match_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.PlayArrow, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Start Game", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }

    if (showRulesDialog) {
      RulesDialog(onDismiss = { showRulesDialog = false })
    }
  }
}

@Composable
private fun QuadrantDot(color: Color) {
  Box(
    modifier = Modifier
      .size(10.dp)
      .clip(CircleShape)
      .background(color)
      .border(1.dp, Color.White, CircleShape)
  )
}

@Composable
private fun GameModeCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  gradient: List<Color>,
  badge: String,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = LudoCardBg),
    modifier = Modifier
      .fillMaxWidth()
      .shadow(4.dp, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Icon Box
      Box(
        modifier = Modifier
          .size(52.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(28.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Content
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0x33FFFFFF))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              color = LudoGold,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 9.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = subtitle,
          color = TextSecondary,
          fontSize = 12.sp,
          maxLines = 1
        )
      }
    }
  }
}
