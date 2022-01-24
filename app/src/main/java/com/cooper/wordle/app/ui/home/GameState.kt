package com.cooper.wordle.app.ui.home

import androidx.compose.ui.graphics.Color
import com.cooper.wordle.app.ui.theme.ColorTone3
import com.cooper.wordle.app.ui.theme.ColorTone4
import com.cooper.wordle.app.ui.theme.DarkGreen
import com.cooper.wordle.app.ui.theme.DarkenedYellow

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

data class WordState(val tileStates: List<TileState>) {
    val letterCount: Int
        get() = tileStates.count { it !is TileState.Empty }

    val isCorrect: Boolean
        get() = tileStates.all { it is TileState.Correct }
}