package com.cooper.wordle.app.ui.game

import androidx.compose.ui.graphics.Color
import com.cooper.wordle.app.ui.theme.ColorTone3
import com.cooper.wordle.app.ui.theme.ColorTone4
import com.cooper.wordle.app.ui.theme.DarkGreen
import com.cooper.wordle.app.ui.theme.DarkenedYellow

sealed class GameState {
    object Uninitialised : GameState()

    data class InProgress(
        val solution: String,
        val gridState: GridState,
        val rowIndex: Int,
    ) : GameState()

    data class Won(
        val solution: String,
        val gridState: GridState
    ) : GameState()

    data class Lost(
        val solution: String,
        val gridState: GridState
    ) : GameState()
}

typealias Grid = List<Row>

data class GridState(
    val height: Int,
    val length: Int,
    val grid: Grid
) {

    fun getRow(rowIndex: Int): Row {
        if (rowIndex >= grid.size)
            throw IllegalStateException("rowIndex $rowIndex is out of bounds ${grid.size}")
        return grid[rowIndex]
    }

    companion object {
        fun empty(height: Int, length: Int): GridState {
            if (height <= 0) throw IllegalArgumentException("height must be a positive number")
            if (length <= 0) throw IllegalArgumentException("length must be a positive number")

            return GridState(
                height,
                length,
                List(height) { Row.emptyWord(length) }
            )
        }
    }
}

data class Row(val tileStates: List<TileState>) {

    val letterCount: Int
        get() = tileStates.count { it !is TileState.Empty }

    val isPopulated: Boolean
        get() = tileStates.any { it is TileState.Foo }

    val isFullyPopulated: Boolean
        get() = tileStates.all { it is TileState.Foo }

    /**
     * Whether this row has space for any more letters
     */
    val hasSpace: Boolean
        get() = tileStates.any { it is TileState.Empty }

    val isCorrect: Boolean
        get() = tileStates.all { it is TileState.Correct }

    companion object {
        fun emptyWord(size: Int): Row {
            return Row(List(size) { TileState.Empty })
        }
    }
}

sealed interface TileState {
    val char: Char?
    val tileBackground: Color
    val tileBorder: Color

    object Empty : TileState {
        override val char: Char? = null
        override val tileBackground = Color.Transparent
        override val tileBorder = ColorTone4
    }

    data class Foo(override val char: Char) : TileState {
        override val tileBackground = Color.Transparent
        override val tileBorder = ColorTone3
    }

    data class Absent(override val char: Char) : TileState {
        override val tileBackground = ColorTone4
        override val tileBorder: Color = tileBackground
    }

    data class Present(override val char: Char) : TileState {
        override val tileBackground = DarkenedYellow
        override val tileBorder: Color = tileBackground
    }

    data class Correct(override val char: Char) : TileState {
        override val tileBackground = DarkGreen
        override val tileBorder: Color = tileBackground
    }
}
