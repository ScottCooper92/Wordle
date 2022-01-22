package com.cooper.wordle.app.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.cooper.wordle.app.R
import com.cooper.wordle.app.ui.components.Key
import com.cooper.wordle.app.ui.components.Keyboard
import com.cooper.wordle.app.ui.components.WordGrid
import com.cooper.wordle.app.ui.theme.WordleTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(state, onKeyClicked = { key ->
        viewModel.onKeyClicked(key)
    })
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class
)
@Composable
private fun HomeScreen(state: HomeViewState, onKeyClicked: (key: Key) -> Unit) {
    Scaffold(
        topBar = {
            HomeAppBar()
        }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            // Create references for the composables to constrain
            val (grid, keyboard) = createRefs()
            val horizontalPadding = 0.05f
            val guidelineTop = createGuidelineFromTop(0.05f)
            val guidelineBottom = createGuidelineFromBottom(0.03f)
            val guidelineStart = createGuidelineFromStart(horizontalPadding)
            val guidelineEnd = createGuidelineFromEnd(horizontalPadding)

            val keyboardBarrier = createTopBarrier(keyboard)

            WordGrid(
                wordLength = 5,
                wordStates = state.wordStates,
                modifier = Modifier
                    .constrainAs(grid) {
                        linkTo(
                            start = guidelineStart,
                            end = guidelineEnd,
                            startMargin = 32.dp,
                            endMargin = 32.dp
                        )
                        linkTo(
                            top = guidelineTop,
                            bottom = keyboardBarrier,
                            bottomMargin = 24.dp,
                            bias = 0f
                        )

                        width = Dimension.fillToConstraints
                        height = Dimension.ratio("5:6")
                    }
            )

            Keyboard(
                onKeyClicked = onKeyClicked,
                modifier = Modifier
                    .constrainAs(keyboard) {
                        linkTo(
                            start = guidelineStart,
                            end = guidelineEnd,
                            startMargin = 24.dp,
                            endMargin = 24.dp
                        )
                        linkTo(top = grid.bottom, bottom = guidelineBottom, bias = 1f)

                        width = Dimension.matchParent
                        height = Dimension.ratio("16:5")
                    }
            )
        }
    }
}

@Composable
private fun HomeAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.wordle).uppercase())
        },
    )
}

@Preview(name = "light")
@Preview(name = "portrait small", heightDp = 400, widthDp = 225)
@Preview(name = "portrait medium", heightDp = 800, widthDp = 450)
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreen() {
    val first = WordState(
        listOf(
            TileState.Absent('P'),
            TileState.Present('A'),
            TileState.Present('R'),
            TileState.Correct('T'),
            TileState.Absent('Y'),
        )
    )

    val second = WordState(
        listOf(
            TileState.Foo('P'),
            TileState.Foo('A'),
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
        )
    )
    val third = WordState(
         listOf(
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
            TileState.Empty
        )
    )

    val state = HomeViewState(
        completeWords = listOf(first),
        currentWord = second,
        remainingGuesses = listOf(third, third, third, third)
    )
    WordleTheme {
        HomeScreen(state) {}
    }
}
