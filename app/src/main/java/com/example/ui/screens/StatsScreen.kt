package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchHistoryEntity
import com.example.data.PlayerProfileEntity
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.LudoCardBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoRoyalBg
import com.example.ui.theme.LudoSurfaceBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
  profile: PlayerProfileEntity?,
  matchHistory: List<MatchHistoryEntity>,
  onBack: () -> Unit,
  onClearHistory: () -> Unit
) {
  BackHandler(onBack = onBack)

  val p = profile ?: PlayerProfileEntity()
  val winRate = if (p.matchesPlayed > 0) ((p.matchesWon.toFloat() / p.matchesPlayed) * 100).toInt() else 0

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            text = "Game Statistics",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("stats_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          if (matchHistory.isNotEmpty()) {
            IconButton(onClick = onClearHistory) {
              Icon(Icons.Default.Delete, contentDescription = "Clear History", tint = TextMuted)
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LudoRoyalBg)
      )
    },
    containerColor = LudoRoyalBg
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Player Summary Header Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(LudoCardBg),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = "👑", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = p.playerName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                  Text(
                    text = "Ludo King Master",
                    color = LudoGold,
                    fontSize = 12.sp
                  )
                }
              }

              // Coins & Diamonds Badge
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = "🪙", fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "${p.coins}",
                    color = LudoGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = "💎", fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "${p.diamonds}",
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of 4 quick stats
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              StatPill(
                icon = Icons.Default.EmojiEvents,
                label = "Wins",
                value = "${p.matchesWon}/${p.matchesPlayed}",
                tint = LudoGold
              )
              StatPill(
                icon = Icons.Default.Shield,
                label = "Win Rate",
                value = "$winRate%",
                tint = AccentCyan
              )
              StatPill(
                icon = Icons.Default.Casino,
                label = "Sixes",
                value = "${p.sixesRolled}",
                tint = Color(0xFF64B5F6)
              )
              StatPill(
                icon = Icons.Default.Bolt,
                label = "Captures",
                value = "${p.pawnsCaptured}",
                tint = LudoRed
              )
            }
          }
        }
      }

      // 2. Streaks card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = LudoCardBg),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF7043))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(text = "Current Streak", color = TextSecondary, fontSize = 11.sp)
                Text(text = "${p.currentStreak} wins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
            Box(modifier = Modifier.height(28.dp).width(1.dp).background(Color(0x33FFFFFF)))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LudoGold)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(text = "Best Streak", color = TextSecondary, fontSize = 11.sp)
                Text(text = "${p.bestStreak} wins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }

      // 3. Match History Header
      item {
        Text(
          text = "Recent Matches",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      if (matchHistory.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🎲", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(text = "No matches played yet!", color = TextSecondary, fontSize = 14.sp)
              Text(text = "Start a game to see your record here.", color = TextMuted, fontSize = 12.sp)
            }
          }
        }
      } else {
        items(matchHistory) { match ->
          MatchHistoryItem(match)
        }
      }
    }
  }
}

@Composable
private fun StatPill(
  icon: ImageVector,
  label: String,
  value: String,
  tint: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(tint.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    Text(text = label, color = TextMuted, fontSize = 10.sp)
  }
}

@Composable
private fun MatchHistoryItem(match: MatchHistoryEntity) {
  val dateStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(match.timestamp))
  val isVictory = match.winnerName.contains("You") || match.winnerName.contains("Player 1")

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = LudoSurfaceBg),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isVictory) LudoGold.copy(alpha = 0.2f) else LudoCardBg),
          contentAlignment = Alignment.Center
        ) {
          Text(text = if (isVictory) "👑" else "🎲", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = match.gameMode,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Text(
            text = "Winner: ${match.winnerName} (${match.winnerColor})",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = if (isVictory) "VICTORY" else "COMPLETED",
          color = if (isVictory) LudoGold else TextMuted,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
        Text(
          text = dateStr,
          color = TextMuted,
          fontSize = 10.sp
        )
      }
    }
  }
}
