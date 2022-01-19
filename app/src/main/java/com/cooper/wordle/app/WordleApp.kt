package com.cooper.wordle.app

import android.app.Application
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cooper.wordle.app.navigation.AppNavigation
import com.google.accompanist.insets.ui.Scaffold
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WordleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

@Composable
fun WordleApp(appState: WordleAppState = rememberWordleAppState()) {
    val navController = rememberNavController()
    navController.navigatorProvider

    Scaffold {
        AppNavigation(
            navController = navController,
            modifier = Modifier
                .fillMaxHeight(),
        )
    }
}
