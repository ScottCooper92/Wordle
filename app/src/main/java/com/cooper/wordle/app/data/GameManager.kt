package com.cooper.wordle.app.data

import com.cooper.wordle.app.ui.game.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class GameStateException(override val message: String) : Throwable()
class InvalidGuess(override val message: String) : Throwable()
class IncorrectGuess(override val message: String) : Throwable()

@Singleton
class GameManager @Inject constructor(private val wordStore: WordStore) {

    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.Uninitialised)
    val state: StateFlow<GameState>
        get() = _state

    suspend fun newGame(letters: WordStore.Letters) {
        val word = wordStore.randomWord(letters)
        val state = GameState.InProgress(
            solution = word,
            gridState = GridState.empty(
                height = MAX_GUESSES,
                length = word.length,
            ),
            rowIndex = 0
        )
        _state.emit(state)
    }

    suspend fun addLetter(char: Char) {
        val currentState = state.value
        if (currentState !is GameState.InProgress)
            throw GameStateException("Game is not in progress $state")

        val gridState = currentState.gridState
        val row = gridState.getRow(currentState.rowIndex)

        if (!row.hasSpace) {
            Timber.d("Word is full")
            return
        }

        // add the tile by replacing the first empty tile
        val newTile = TileState.Foo(char)
        val updatedRow = row.copy(
            tileStates = row.tileStates.toMutableList().apply {
                val index = indexOfFirst { it is TileState.Empty }
                set(index, newTile)
            }
        )

        // replace the old grid with the new grid
        val updatedState = currentState.copy(
            gridState = updateGridState(updatedRow)
        )

        _state.emit(updatedState)
    }

    suspend fun removeLetter() {
        val currentState = state.value
        if (currentState !is GameState.InProgress)
            throw GameStateException("Game is not in progress $state")

        val gridState = currentState.gridState
        val row = gridState.getRow(currentState.rowIndex)

        if (!row.isPopulated) {
            Timber.d("Word is empty")
            return
        }

        // add the tile by replacing the first empty tile
        val updatedRow = row.copy(
            tileStates = row.tileStates.toMutableList().apply {
                val index = indexOfLast { it is TileState.Foo }
                set(index, TileState.Empty)
            }
        )

        // replace the old grid with the new grid
        val updatedState = currentState.copy(
            gridState = updateGridState(updatedRow)
        )

        _state.emit(updatedState)
    }

    suspend fun evaluateCurrentRow() {
        val currentState = state.value
        if (currentState !is GameState.InProgress)
            throw GameStateException("Game is not in progress $state")

        val gridState = currentState.gridState
        val row = gridState.getRow(currentState.rowIndex)

        // evaluate the guess
        val updatedRow = evaluateGuess(row, currentState.solution)
        val updatedGrid = updateGridState(updatedRow)

        val nextIndex = currentState.rowIndex + 1
        val updatedState: GameState = when {
            updatedRow.isCorrect -> {
                // the guess is correct update to won state
                GameState.Won(currentState.solution, updatedGrid)
            }
            nextIndex >= gridState.height -> {
                // no guesses left, update to lost state
                GameState.Lost(currentState.solution, updatedGrid)
            }
            else -> {
                // progress to the next guess
                currentState.copy(
                    gridState = updatedGrid,
                    rowIndex = nextIndex
                )
            }
        }

        _state.emit(updatedState)
    }

    /**
     * TODO
     */
    private fun updateGridState(updatedRow: Row): GridState {
        val currentState = state.value
        if (currentState !is GameState.InProgress)
            throw GameStateException("Game is not in progress $state")

        val gridState = currentState.gridState

        // replace the old row with the new row
        return gridState.copy(
            grid = gridState.grid.toMutableList().apply {
                set(currentState.rowIndex, updatedRow)
            },
        )
    }

    /**
     * TODO - Solution POSEY, guess POPPY highlighting both Ps as present
     */
    private suspend fun evaluateGuess(guess: Row, answer: String): Row {
        if (guess.tileStates.size != answer.length)
            throw IllegalArgumentException("Guess isn't the same length as answer. Answer: $answer, Guess: $guess")

        if (!guess.isFullyPopulated)
            throw InvalidGuess("Not enough letters in $guess")

        if (!wordStore.wordExists(guess.toString()))
            throw IncorrectGuess("Not a word")

        val tiles = guess.tileStates.toMutableList()
        val result = tiles.mapIndexed { index, tileState ->

            // check if this tile is correct
            val correctTile = isTileCorrect(tileState, answer[index])
            if (correctTile != null) return@mapIndexed correctTile

            // check for any occurrences of this char in the answer
            val fooTile = tileState as TileState.Foo
            var occurrence = answer.indexOf(fooTile.char, ignoreCase = true)
            while (occurrence >= 0) {

                // check if the tile at this index is correct as that takes priority
                val tileAtSameIndex = tiles[occurrence]
                val checkedTile = isTileCorrect(tileAtSameIndex, answer[occurrence])
                if (checkedTile != null) {
                    // since we've checked this tile we might as well add it to the result
                    tiles[occurrence] = checkedTile
                    // check for any other occurrences
                    occurrence = answer.indexOf(fooTile.char, occurrence + 1, true)
                } else {
                    // there isn't a better match so this can be considered present
                    return@mapIndexed TileState.Present(fooTile.char)
                }
            }

            // incorrect and not present
            return@mapIndexed TileState.Absent(fooTile.char)
        }

        return Row(result)
    }

    private fun isTileCorrect(tile: TileState, char: Char): TileState? {
        // check if the tile has already been checked
        if (tile is TileState.Present || tile is TileState.Correct) {
            Timber.d("Tile $tile already checked")
            return tile
        }

        val fooTile = tile as TileState.Foo
        return if (fooTile.char.equals(char, true)) {
            // tile is in the correct location
            TileState.Correct(tile.char)
        } else null
    }

    companion object {
        private const val MAX_GUESSES = 6
    }
}