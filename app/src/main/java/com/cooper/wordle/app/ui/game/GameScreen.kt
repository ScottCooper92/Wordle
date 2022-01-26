package com.cooper.wordle.app.ui.game

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.cooper.wordle.app.data.GridState
import com.cooper.wordle.app.data.Row
import com.cooper.wordle.app.data.TileState
import com.cooper.wordle.app.ui.components.Key
import com.cooper.wordle.app.ui.components.Keyboard
import com.cooper.wordle.app.ui.components.WordGrid
import com.cooper.wordle.app.ui.components.WordleAppBar
import com.cooper.wordle.app.ui.theme.WordleTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Timber.d("$state")
    GameScreen(
        state,
        viewModel.effects,
        onActionClicked = { },
        onKeyClicked = { key ->
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
private fun GameScreen(
    state: GameViewState,
    effects: Flow<GameUiEffect>,
    onActionClicked: () -> Unit,
    onKeyClicked: (key: Key) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is GameUiEffect.ShowMessage -> {
                    launch {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
                GameUiEffect.ClearMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            WordleAppBar(onActionClicked)
        },
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackBarData -> Snackbar(snackbarData = snackBarData) },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            when (state) {
                GameViewState.Loading -> {
                    Loading()
                }
                is GameViewState.InProgress -> GameBoard(state.gridState, onKeyClicked)
            }
        }
    }
}

@Composable
private fun ConstraintLayoutScope.Loading() {
    CircularProgressIndicator(
        modifier = Modifier
            .wrapContentSize()
            .constrainAs(createRef()) {
                linkTo(parent.start, parent.end)
                linkTo(parent.top, parent.bottom)
            }
    )
}

@Composable
private fun ConstraintLayoutScope.GameBoard(
    gridState: GridState,
    onKeyClicked: (key: Key) -> Unit
) {
    // Create references for the composables to constrain
    val (grid, keyboard) = createRefs()
    val horizontalPadding = 0.05f
    val guidelineTop = createGuidelineFromTop(0.05f)
    val guidelineBottom = createGuidelineFromBottom(0.03f)
    val guidelineStart = createGuidelineFromStart(horizontalPadding)
    val guidelineEnd = createGuidelineFromEnd(horizontalPadding)

    val keyboardBarrier = createTopBarrier(keyboard)

    WordGrid(
        gridState = gridState,
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

@Preview(name = "light")
@Preview(name = "portrait small", heightDp = 400, widthDp = 225)
@Preview(name = "portrait medium", heightDp = 800, widthDp = 450)
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewGameScreen() {
    val first = Row(
        listOf(
            TileState.Absent('P'),
            TileState.Present('A'),
            TileState.Present('R'),
            TileState.Correct('T'),
            TileState.Absent('Y'),
        )
    )

    val second = Row(
        listOf(
            TileState.Foo('P'),
            TileState.Foo('A'),
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
        )
    )
    val third = Row(
        listOf(
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
            TileState.Empty
        )
    )

    val state = GameViewState.InProgress(
        solution = "Answer",
        gridState = GridState(6, 5, listOf(first, second, third, third, third, third))
    )
    WordleTheme {
        GameScreen(
            state = state,
            effects = flow {},
            onActionClicked = {},
            onKeyClicked = {}
        )
    }
}

@Preview("loading")
@Composable
fun PreviewGameScreenLoading() {
    WordleTheme {
        GameScreen(
            state = GameViewState.Loading,
            effects = flow {},
            onActionClicked = {},
            onKeyClicked = {}
        )
    }
}
