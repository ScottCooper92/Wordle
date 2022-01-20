package com.cooper.wordle.app.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cooper.wordle.app.R
import com.cooper.wordle.app.ui.components.Keyboard
import com.cooper.wordle.app.ui.components.WordRow
import com.cooper.wordle.app.ui.theme.WordleTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(state: HomeViewState) {

    Surface(tonalElevation = 5.dp) {
        // content
    }

    Scaffold(
        topBar = {
            HomeAppBar()
        }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                val modifier = Modifier.weight(1f)
                state.wordStates.forEach { wordState ->
                    WordRow(wordState, modifier)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val height = LocalConfiguration.current.screenHeightDp / 4
            Keyboard(
                onKeyClicked = { key -> },
                modifier = Modifier.height(height.dp)
            )
        }
    }
}

@Composable
private fun HomeAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.wordle).uppercase())
        }
    )
}

@Preview(name = "portrait light")
@Preview(name = "home screen dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreen() {
    val first = WordState(
        true, listOf(
            TileState.Absent('P'),
            TileState.Present('A'),
            TileState.Present('R'),
            TileState.Correct('T'),
            TileState.Absent('Y'),
        )
    )

    val second = WordState(
        false, listOf(
            TileState.Foo('P'),
            TileState.Foo('A'),
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
        )
    )
    val third = WordState(
        false, listOf(
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
            TileState.Empty
        )
    )

    val state = HomeViewState(wordStates = listOf(first, second, third, third, third))
    WordleTheme {
        HomeScreen(state)
    }
}
