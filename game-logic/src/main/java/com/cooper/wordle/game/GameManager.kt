package com.cooper.wordle.game

import com.cooper.wordle.game.EvaluatedLetterState.*
import com.cooper.wordle.game.data.Letters
import com.cooper.wordle.game.data.WordStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class GameStateException(override val message: String) : Throwable()
class InvalidGuess(override val message: String) : Throwable()
class IncorrectGuess(override val message: String) : Throwable() //TODO - Should this also be invalid?

@Singleton
class GameManager @Inject internal constructor(private val wordStore: WordStore) {

    private val _state: MutableStateFlow<GameState> = MutableStateFlow(GameState.Uninitialised)
    val state: StateFlow<GameState>
        get() = _state

    /**
     * Starts a new game by selecting a word of size n, where n is the value of [letters].
     * Observe [GameManager.state] to receive updates to the state of the game.
     *
     * @param letters - The number of letters the answer will consist of.
     */
    suspend fun newGame(letters: Letters) {
        val word = wordStore.randomWord(letters)
        val state = GameState.InProgress(
            solution = word,
            usedLetters = emptySet(),
            gridState = GridState.empty(
                height = MAX_GUESSES,
                length = word.length,
            ),
            rowIndex = 0
        )
        _state.emit(state)
    }

    /**
     * Adds a letter to the current row.
     *
     * @param char - The letter to be added
     * @throws GameStateException If a game is not currently in progress.
     */
    suspend fun addLetter(char: Char) {
        val currentState = state.value
        if (currentState !is GameState.InProgress)
            throw GameStateException("Game is not in progress $state")

        val gridState = currentState.gridState
        val row = gridState.getRow(currentState.rowIndex)

        // check if we have space left to add a new character
        if (!row.hasSpace) {
            Timber.d("Word is full")
            return
        }

        // add the tile by replacing the first empty tile
        val newTile = ProspectiveLetter(char)
        val updatedRow = row.copy(
            tileStates = row.tileStates.toMutableList().apply {
                val index = indexOfFirst { it is EmptyLetter }
                set(index, newTile)
            }
        )

        // replace the old grid with the new grid
        val updatedState = currentState.copy(
            gridState = updateGridState(updatedRow)
        )

        _state.emit(updatedState)
    }

    /**
     * Removes the last added letter from the current row.
     *
     * @throws GameStateException If a game is not currently in progress.
     */
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
                val index = indexOfLast { it is ProspectiveLetter }
                set(index, EmptyLetter)
            }
        )

        // replace the old grid with the new grid
        val updatedState = currentState.copy(
            gridState = updateGridState(updatedRow)
        )

        _state.emit(updatedState)
    }

    /**
     * Evaluates the current row of letters by comparing them against the answer.
     *
     * @@throws GameStateException If a game is not currently in progress.
     * @throws InvalidGuess If the row is incomplete
     * @throws IncorrectGuess If the word entered is not a valid word.
     */
    suspend fun evaluateCurrentRow() {
        val currentState = state.value
        if (currentState !is GameState.InProgress)
            throw GameStateException("Game is not in progress $state")

        val gridState = currentState.gridState
        val row = gridState.getRow(currentState.rowIndex)

        // evaluate the guess
        val evaluatedLetters = evaluateGuess(row, currentState.solution)
        val evaluatedRow = GridRow(evaluatedLetters)
        val updatedGrid = updateGridState(evaluatedRow)
        val updatedLetters = updateEvaluatedLetters(currentState.usedLetters, evaluatedLetters)

        val nextIndex = currentState.rowIndex + 1
        val updatedState: GameState = when {
            evaluatedRow.isCorrect -> {
                // the guess is correct update to won state
                GameState.Won(currentState.solution, updatedLetters, updatedGrid)
            }
            nextIndex >= gridState.height -> {
                // no guesses left, update to lost state
                GameState.Lost(currentState.solution, updatedLetters, updatedGrid)
            }
            else -> {
                // progress to the next guess
                currentState.copy(
                    gridState = updatedGrid,
                    usedLetters = updatedLetters,
                    rowIndex = nextIndex
                )
            }
        }

        _state.emit(updatedState)
    }

    /**
     * Updates the current value of [GridState] by replacing the current row with [updatedRow]
     * Current row is controlled by GridState.
     */
    private fun updateGridState(updatedRow: GridRow): GridState {
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
     * Merge the existing evaluated letters with the new letters.
     * Existing letters will be overridden if the new [EvaluatedLetterState.value] is greater than
     * the existing value.
     *
     * Values are as follows
     * [CorrectLetter] > [PresentLetter] > [AbsentLetter]
     *
     * This allows us to "upgrade" a [PresentLetter] with a [CorrectLetter] when the correct location
     * has been found.
     */
    private fun updateEvaluatedLetters(
        existingLetters: Set<EvaluatedLetterState>,
        newLetters: List<EvaluatedLetterState>
    ): Set<EvaluatedLetterState> {
        val lettersByChar = existingLetters.associateBy { it.char }.toMutableMap()
        newLetters.forEach { newState ->
            val existing = lettersByChar.putIfAbsent(newState.char, newState)
            if (existing != null && newState.value > existing.value) {
                lettersByChar[newState.char] = newState
            }
        }
        return lettersByChar.values.toSet()
    }

    /**
     * Evaluates [guess] by comparing it to the actual answer.
     *
     * The response will be the same [LetterState]'s in the same order but mapped to an appropriate
     * [EvaluatedLetterState] based on whether the letter was:
     *  - missing from the answer = [AbsentLetter]
     *  - present in the answer but in the wrong location = [PresentLetter]
     *  - present in the answer and in the correct location = [CorrectLetter]
     * TODO - Solution POSEY, guess POPPY highlighting both Ps as present
     */
    private suspend fun evaluateGuess(guess: GridRow, answer: String): List<EvaluatedLetterState> {
        if (guess.tileStates.size != answer.length)
            throw IllegalArgumentException("Guess isn't the same length as answer. Answer: $answer, Guess: $guess")

        if (!guess.isFullyPopulated)
            throw InvalidGuess("Not enough letters in $guess")

        if (!wordStore.wordExists(guess.toString()))
            throw IncorrectGuess("Not a word")

        val tiles = guess.tileStates.toMutableList()
        return tiles.mapIndexed { index, tileState ->

            // check if this tile is correct
            val correctTile = isTileCorrect(tileState, answer[index])
            if (correctTile != null) return@mapIndexed correctTile

            // check for any occurrences of this char in the answer
            val prospectiveTile = tileState as ProspectiveLetter
            var occurrence = answer.indexOf(prospectiveTile.char, ignoreCase = true)
            while (occurrence >= 0) {

                // check if the tile at this index is correct as that takes priority
                val tileAtSameIndex = tiles[occurrence]
                val checkedTile = isTileCorrect(tileAtSameIndex, answer[occurrence])
                if (checkedTile != null) {
                    // since we've checked this tile we might as well add it to the result
                    tiles[occurrence] = checkedTile
                    // check for any other occurrences
                    occurrence = answer.indexOf(prospectiveTile.char, occurrence + 1, true)
                } else {
                    // there isn't a better match so this can be considered present
                    return@mapIndexed PresentLetter(prospectiveTile.char)
                }
            }

            // incorrect and not present
            return@mapIndexed AbsentLetter(prospectiveTile.char)
        }
    }

    /**
     * Returns a [CorrectLetter] LetterState if the [tile] matches [char].
     */
    private fun isTileCorrect(tile: LetterState, char: Char): EvaluatedLetterState? {
        // check if the tile has already been checked
        if (tile is PresentLetter || tile is CorrectLetter) {
            Timber.d("Tile $tile already checked")
            return tile as EvaluatedLetterState
        }

        val prospectiveTile = tile as ProspectiveLetter
        return if (prospectiveTile.char.equals(char, true)) {
            // tile is in the correct location
            CorrectLetter(tile.char)
        } else null
    }

    companion object {
        private const val MAX_GUESSES = 6
    }
}