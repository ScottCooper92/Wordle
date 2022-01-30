package com.cooper.wordle.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cooper.wordle.app.ui.game.GameScreen
import com.cooper.wordle.app.ui.help.HelpScreen
import com.cooper.wordle.app.ui.start.StartScreen
import com.cooper.wordle.game.data.Letters
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

private sealed class Screen(val route: String) {

    object Start : Screen("start")
    object Game : Screen("game/{letters}") {
        fun createRoute(letters: Letters): String {
            return "game/$letters"
        }
    }

    object Help : Screen("help")
    object Settings : Screen("settings")
    object Stats : Screen("stats")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Start.route,
        modifier = modifier,
    ) {

        composable(Screen.Start.route) {
            StartScreen(
                onLettersPicked = { letters ->
                    navController.navigate(Screen.Game.createRoute(letters))
                },
                onHelpClicked = {
                    navController.navigate(Screen.Help.route)
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("letters") { type = NavType.EnumType(Letters::class.java) }
            )
        ) {
            GameScreen(
                onHelpClicked = {
                    navController.navigate(Screen.Help.route)
                }
            )
        }

        bottomSheet(Screen.Help.route) {
            HelpScreen()
        }

        composable(Screen.Settings.route) {

        }

        composable(Screen.Stats.route) {

        }
    }
}