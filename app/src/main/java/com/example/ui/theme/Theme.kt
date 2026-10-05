package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LudoKingColorScheme = darkColorScheme(
  primary = LudoGold,
  onPrimary = Color.Black,
  primaryContainer = LudoCardBg,
  onPrimaryContainer = LudoGoldLight,
  secondary = AccentCyan,
  onSecondary = Color.Black,
  secondaryContainer = LudoSurfaceBg,
  onSecondaryContainer = Color.White,
  tertiary = LudoRed,
  onTertiary = Color.White,
  background = LudoRoyalBg,
  onBackground = TextPrimary,
  surface = LudoSurfaceBg,
  onSurface = TextPrimary,
  surfaceVariant = LudoCardBg,
  onSurfaceVariant = TextSecondary,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = LudoKingColorScheme,
    typography = Typography,
    content = content
  )
}
