package com.cooper.wordle.app.interactor

import com.cooper.wordle.app.ui.game.GameViewState
import com.cooper.wordle.game.GameManager
import com.cooper.wordle.game.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class ObserveGameState @Inject constructor(
    private val gameManager: GameManager
) : FlowInteractor<Unit?, GameViewState>() {

    override fun createFlow(params: Unit?): Flow<GameViewState> =
        gameManager.state.mapLatest(::mapState)

    private fun mapState(gameState: GameState): GameViewState {
        return when (gameState) {
            GameState.Uninitialised -> GameViewState.Loading
            is GameState.InProgress -> GameViewState.InProgress(
                solution = gameState.solution,
                usedLetters = gameState.usedLetters,
                gridState = gameState.gridState
            )
            is GameState.Won -> GameViewState.InProgress(
                solution = gameState.solution,
                usedLetters = gameState.usedLetters,
                gridState = gameState.gridState
            )
            is GameState.Lost -> GameViewState.InProgress(
                solution = gameState.solution,
                usedLetters = gameState.usedLetters,
                gridState = gameState.gridState
            )
        }
    }

   /* private fun mapUsedLetters(usedLetters: Set<EvaluatedLetterState>): Set<EvaluatedLetterState> {
        return mutableSetOf<EvaluatedLetterState>().apply {
            addAll(usedLetters.map { mapLetterState(it) as EvaluatedLetterState })
        }
    }

    private fun mapGridState(gridState: GridState): GridViewState {
        return GridViewState(
            height = gridState.height,
            length = gridState.length,
            grid = gridState.grid.map { row ->
                GridRow(row.tileStates.map { letters -> mapLetterState(letters) })
            }
        )
    }

    private fun mapLetterState(letterState: LetterState): LetterViewState {
        return when (letterState) {
            LetterState.Empty -> LetterViewState.Empty
            is LetterState.Prospective -> LetterViewState.Prospective(letterState.char)
            is EvaluatedLetterState.Absent -> LetterViewState.Absent(letterState.char)
            is EvaluatedLetterState.Correct -> LetterViewState.Correct(letterState.char)
            is EvaluatedLetterState.Present -> LetterViewState.Present(letterState.char)
        }
    }*/
}