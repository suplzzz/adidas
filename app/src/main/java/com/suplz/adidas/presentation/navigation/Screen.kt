package com.suplz.adidas.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    // Убираем аргументы, они больше не нужны
    object Timeline : Screen("timeline_screen")
}