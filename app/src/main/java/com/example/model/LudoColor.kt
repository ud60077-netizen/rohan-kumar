package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LudoBlue
import com.example.ui.theme.LudoBlueDark
import com.example.ui.theme.LudoBlueLight
import com.example.ui.theme.LudoGreen
import com.example.ui.theme.LudoGreenDark
import com.example.ui.theme.LudoGreenLight
import com.example.ui.theme.LudoRed
import com.example.ui.theme.LudoRedDark
import com.example.ui.theme.LudoRedLight
import com.example.ui.theme.LudoYellow
import com.example.ui.theme.LudoYellowDark
import com.example.ui.theme.LudoYellowLight

enum class LudoColor(
  val displayName: String,
  val primaryColor: Color,
  val lightColor: Color,
  val darkColor: Color,
  val startCellIndex: Int,   // Cell index on global 52-cell track where unlocked pawn starts
  val homeEntryIndex: Int   // Global track cell right before turning into colored home stretch
) {
  RED(
    displayName = "Red",
    primaryColor = LudoRed,
    lightColor = LudoRedLight,
    darkColor = LudoRedDark,
    startCellIndex = 0,
    homeEntryIndex = 50
  ),
  GREEN(
    displayName = "Green",
    primaryColor = LudoGreen,
    lightColor = LudoGreenLight,
    darkColor = LudoGreenDark,
    startCellIndex = 13,
    homeEntryIndex = 11
  ),
  YELLOW(
    displayName = "Yellow",
    primaryColor = LudoYellow,
    lightColor = LudoYellowLight,
    darkColor = LudoYellowDark,
    startCellIndex = 26,
    homeEntryIndex = 24
  ),
  BLUE(
    displayName = "Blue",
    primaryColor = LudoBlue,
    lightColor = LudoBlueLight,
    darkColor = LudoBlueDark,
    startCellIndex = 39,
    homeEntryIndex = 37
  )
}
