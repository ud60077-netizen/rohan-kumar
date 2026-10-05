package com.example.model

enum class GameMode(
  val title: String,
  val subtitle: String,
  val iconName: String
) {
  VS_COMPUTER(
    title = "Vs Computer",
    subtitle = "Challenge smart bot AI opponents",
    iconName = "smart_toy"
  ),
  PASS_AND_PLAY(
    title = "Pass & Play",
    subtitle = "Local multiplayer with friends & family",
    iconName = "group"
  ),
  QUICK_LUDO(
    title = "Quick Ludo",
    subtitle = "Fast 2-token rush matches",
    iconName = "bolt"
  ),
  SNAKES_AND_LADDERS(
    title = "Snakes & Ladders",
    subtitle = "Classic 100-square board race",
    iconName = "casino"
  )
}
