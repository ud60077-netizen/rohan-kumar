package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.theme.LudoCardBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.TextMuted

@Composable
fun PlayerCardView(
  player: Player,
  isCurrentTurn: Boolean,
  diceValue: Int? = null,
  isRolling: Boolean = false,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val borderAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "borderAlpha"
  )

  val cardBg = if (isCurrentTurn) {
    Brush.linearGradient(
      listOf(
        LudoCardBg,
        player.color.primaryColor.copy(alpha = 0.25f)
      )
    )
  } else {
    Brush.linearGradient(
      listOf(LudoCardBg, Color(0xFF1B1335))
    )
  }

  val borderColor = if (isCurrentTurn) {
    player.color.lightColor.copy(alpha = borderAlpha)
  } else {
    Color(0x33FFFFFF)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(cardBg)
      .border(
        width = if (isCurrentTurn) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Avatar with Color Rim
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(player.color.darkColor)
          .border(2.dp, player.color.lightColor, CircleShape)
      ) {
        if (player.rank > 0) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Rank",
            tint = LudoGold,
            modifier = Modifier.size(22.dp)
          )
        } else if (player.isBot) {
          Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "Bot",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        } else {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Human",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Name & Tokens progress
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = player.name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          if (player.rank > 0) {
            Text(
              text = "#${player.rank}",
              color = LudoGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 4 tokens status dots (Filled = In Home, Outlined = Active/Yard)
        Row(
          horizontalArrangement = Arrangement.spacedBy(3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (pawn in player.pawns) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                  if (pawn.isFinished) LudoGold
                  else if (!pawn.inYard) player.color.lightColor
                  else Color(0x55FFFFFF)
                )
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${player.finishedPawnsCount}/${player.pawns.size}",
            color = TextMuted,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
