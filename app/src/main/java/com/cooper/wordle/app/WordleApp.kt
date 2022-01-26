package com.cooper.wordle.app

import android.app.Application
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.cooper.wordle.app.navigation.AppNavigation
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
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

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun WordleApp(appState: WordleAppState = rememberWordleAppState()) {
    val bottomSheetController = rememberBottomSheetNavigator()
    val navController = rememberNavController()
    navController.navigatorProvider += bottomSheetController

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetController,
        sheetShape = MaterialTheme.shapes.copy(large = RoundedCornerShape(8.dp)).large,
        sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        sheetContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    ) {
        Scaffold {
            AppNavigation(
                navController = navController,
                modifier = Modifier
                    .fillMaxHeight(),
            )
        }
    }
}
