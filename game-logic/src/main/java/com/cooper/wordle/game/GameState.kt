package com.cooper.wordle.game

sealed class GameState {
    object Uninitialised : GameState()

    data class InProgress internal constructor(
        val solution: String,
        val usedLetters: Set<EvaluatedLetterState>,
        val gridState: GridState,
        val rowIndex: Int,
    ) : GameState()

    data class Won internal constructor(
        val solution: String,
        val usedLetters: Set<EvaluatedLetterState>,
        val gridState: GridState
    ) : GameState()

    data class Lost internal constructor(
        val solution: String,
        val usedLetters: Set<EvaluatedLetterState>,
        val gridState: GridState
    ) : GameState()
}

typealias Grid = List<GridRow>

data class GridState constructor(
    val height: Int,
    val length: Int,
    val grid: Grid
) {

    internal fun getRow(rowIndex: Int): GridRow {
        if (rowIndex >= grid.size)
            throw IllegalStateException("rowIndex $rowIndex is out of bounds ${grid.size}")
        return grid[rowIndex]
    }

    internal companion object {
        fun empty(height: Int, length: Int): GridState {
            if (height <= 0) throw IllegalArgumentException("height must be a positive number")
            if (length <= 0) throw IllegalArgumentException("length must be a positive number")

            return GridState(
                height,
                length,
                List(height) { GridRow.emptyWord(length) }
            )
        }
    }
}

data class GridRow constructor(val tileStates: List<LetterState>) {

    internal val isPopulated: Boolean
        get() = tileStates.any { it is ProspectiveLetter }

    internal val isFullyPopulated: Boolean
        get() = tileStates.all { it is ProspectiveLetter }

    /**
     * Whether this row has space for any more letters
     */
    internal val hasSpace: Boolean
        get() = tileStates.any { it is EmptyLetter }

    internal val isCorrect: Boolean
        get() = tileStates.all { it is CorrectLetter }

    override fun toString(): String {
        return tileStates.joinToString(separator = "") { tile ->
            tile.char?.toString() ?: ""
        }
    }

    internal companion object {
        fun emptyWord(size: Int): GridRow {
            return GridRow(List(size) { EmptyLetter })
        }
    }
}

sealed interface LetterState {
    val char: Char?
}

object EmptyLetter : LetterState {
    override val char: Char? = null
}

data class ProspectiveLetter constructor(override val char: Char) : LetterState

sealed class EvaluatedLetterState(val value: Int) : LetterState
data class AbsentLetter constructor(override val char: Char) : EvaluatedLetterState(0)
data class PresentLetter constructor(override val char: Char) : EvaluatedLetterState(1)
data class CorrectLetter constructor(override val char: Char) : EvaluatedLetterState(2)
