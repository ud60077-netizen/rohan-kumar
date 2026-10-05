package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LudoCardBg
import com.example.ui.theme.LudoGold
import com.example.ui.theme.LudoRoyalBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun RulesDialog(onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = LudoRoyalBg),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "👑", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Ludo Rules",
              color = LudoGold,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RuleItem(
          icon = Icons.Default.Casino,
          title = "Rolling a Six",
          desc = "Roll a 6 to bring a token out of the yard onto the start square. Rolling a 6 also awards an extra roll! Rolling three consecutive 6s skips your turn."
        )

        RuleItem(
          icon = Icons.Default.Bolt,
          title = "Capturing Opponents",
          desc = "Landing on an opponent's token outside a safe square sends it back to their yard, and awards you an extra bonus roll!"
        )

        RuleItem(
          icon = Icons.Default.Star,
          title = "Safe Star Squares",
          desc = "Colored starting squares and squares marked with a star (⭐) are safe. Tokens on safe squares cannot be captured by opponents."
        )

        RuleItem(
          icon = Icons.Default.EmojiEvents,
          title = "Reaching Home",
          desc = "Travel the entire track and enter your colored home corridor. Tokens need an exact roll to enter the center Home. First player to get all tokens home wins King status!"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = LudoGold, contentColor = Color.Black),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = "Got it!", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun RuleItem(
  icon: ImageVector,
  title: String,
  desc: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(LudoCardBg),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = LudoGold, modifier = Modifier.size(20.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = desc,
        color = TextSecondary,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )
    }
  }
}
