package com.cooper.wordle.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cooper.wordle.app.ui.game.GameScreen

internal sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Help : Screen("help")
    object Settings : Screen("settings")
    object Stats : Screen("stats")
}

@Composable
internal fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {

        composable(Screen.Home.route) {
            GameScreen()
        }

        composable(Screen.Help.route) {

        }

        composable(Screen.Settings.route) {

        }

        composable(Screen.Stats.route) {

        }
    }
}