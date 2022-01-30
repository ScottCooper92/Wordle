package com.cooper.wordle.app.ui.game

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.cooper.wordle.app.ui.components.AppBarAction
import com.cooper.wordle.app.ui.components.WordleAppBar
import com.cooper.wordle.app.ui.game.components.Key
import com.cooper.wordle.app.ui.game.components.Keyboard
import com.cooper.wordle.app.ui.game.components.WordGrid
import com.cooper.wordle.app.ui.theme.WordleTheme
import com.cooper.wordle.game.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel(),
    onHelpClicked: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Timber.d("$state")
    GameScreen(
        state,
        viewModel.effects,
        onActionClicked = { action ->
            when (action) {
                AppBarAction.HELP -> onHelpClicked()
            }
        },
        onKeyClicked = { key ->
            viewModel.onKeyClicked(key)
        })
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
private fun GameScreen(
    state: GameViewState,
    effects: Flow<GameUiEffect>,
    onActionClicked: (AppBarAction) -> Unit,
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
        topBar = { WordleAppBar(onActionClicked) },
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackBarData -> Snackbar(snackbarData = snackBarData) },
            modifier = Modifier.fillMaxWidth()
        )

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            when (state) {
                GameViewState.Loading -> Loading()
                is GameViewState.InProgress -> GameBoard(
                    gridState = state.gridState,
                    usedLetters = state.usedLetters,
                    onKeyClicked = onKeyClicked
                )
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
    usedLetters: Set<EvaluatedLetterState>,
    onKeyClicked: (key: Key) -> Unit
) {
    // Create references for the composables to constrain
    val (grid, keyboard) = createRefs()
    val horizontalPadding = 0.05f
    val guidelineTop = createGuidelineFromTop(0.05f)
    val guidelineBottom = createGuidelineFromBottom(0.01f)
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
                    startMargin = 48.dp,
                    endMargin = 48.dp
                )
                linkTo(
                    top = guidelineTop,
                    bottom = keyboardBarrier,
                    bottomMargin = 24.dp,
                    bias = 0f
                )

                width = Dimension.fillToConstraints
                height = Dimension.ratio("${gridState.length}:${gridState.height}")
            }
    )

    Keyboard(
        usedLetters = usedLetters,
        onKeyClicked = onKeyClicked,
        modifier = Modifier
            .constrainAs(keyboard) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    startMargin = 8.dp,
                    endMargin = 8.dp
                )
                linkTo(top = grid.bottom, bottom = guidelineBottom, bias = 1f)

                width = Dimension.fillToConstraints
                height = Dimension.ratio("2:1")
            }
    )
}

@Preview(name = "empty grid length 4")
@Composable
private fun PreviewEmptyGameScreen(wordSize: Int = 4) {
    val emptyRow = GridRow(List(wordSize) { EmptyLetter })
    val state = GameViewState.InProgress(
        solution = "Answer",
        gridState = GridState(6, wordSize, List(6) { emptyRow }),
        usedLetters = emptySet()
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

@Preview(name = "empty grid length 5")
@Composable
private fun PreviewEmptyGameScreenSize5() {
    PreviewEmptyGameScreen(5)
}

@Preview(name = "empty grid length 6")
@Composable
private fun PreviewEmptyGameScreenSize6() {
    PreviewEmptyGameScreen(6)
}

@Preview(name = "empty grid length 7")
@Composable
private fun PreviewEmptyGameScreenSize7() {
    PreviewEmptyGameScreen(7)
}

@Preview(name = "light")
@Preview(name = "portrait small", heightDp = 400, widthDp = 225)
@Preview(name = "portrait medium", heightDp = 800, widthDp = 450)
@Preview(name = "dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewGameScreen() {
    val first = GridRow(
        listOf(
            AbsentLetter('P'),
            PresentLetter('A'),
            PresentLetter('R'),
            CorrectLetter('T'),
            AbsentLetter('Y'),
        )
    )

    val second = GridRow(
        listOf(
            ProspectiveLetter('P'),
            ProspectiveLetter('A'),
            EmptyLetter,
            EmptyLetter,
            EmptyLetter,
        )
    )
    val third = GridRow(
        listOf(
            EmptyLetter,
            EmptyLetter,
            EmptyLetter,
            EmptyLetter,
            EmptyLetter
        )
    )

    val state = GameViewState.InProgress(
        solution = "Answer",
        gridState = GridState(6, 5, listOf(first, second, third, third, third, third)),
        usedLetters = emptySet()
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
