package com.cooper.wordle.app.ui.game.components

import androidx.compose.ui.graphics.Color
import com.cooper.wordle.app.ui.theme.*
import com.cooper.wordle.game.*

data class TileStyle(val background: Color, val border: Color)
data class KeyStyle(val background: Color)

object LetterStyleHelper {

    fun getTileStyle(letterState: LetterState): TileStyle {
        return when (letterState) {
            EmptyLetter -> TileStyle(Color.Transparent, ColorTone4)
            is ProspectiveLetter -> TileStyle(Color.Transparent, ColorTone3)
            is AbsentLetter -> TileStyle(ColorTone4, ColorTone4)
            is PresentLetter -> TileStyle(DarkenedYellow, DarkenedYellow)
            is CorrectLetter -> TileStyle(DarkGreen, DarkGreen)
        }
    }

    fun getKeyStyle(letterState: EvaluatedLetterState?): KeyStyle {
        return when (letterState) {
            is AbsentLetter -> KeyStyle(ColorTone4)
            is PresentLetter -> KeyStyle(DarkenedYellow)
            is CorrectLetter -> KeyStyle(DarkGreen)
            else -> KeyStyle(KeyBackground)
        }
    }
}