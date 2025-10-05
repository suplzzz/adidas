package com.suplz.adidas.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suplz.adidas.presentation.screen.home.HomeScreen
import com.suplz.adidas.presentation.screen.timeline.TimelineScreen // Импортируем

@Composable
fun AppNavigation(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
        // Добавляем новый экран в граф навигации
        composable(route = Screen.Timeline.route) {
            TimelineScreen(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}