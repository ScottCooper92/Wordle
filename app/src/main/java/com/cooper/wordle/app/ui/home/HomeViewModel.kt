package com.cooper.wordle.app.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.cooper.wordle.app.ui.theme.ColorTone3
import com.cooper.wordle.app.ui.theme.ColorTone4
import com.cooper.wordle.app.ui.theme.DarkGreen
import com.cooper.wordle.app.ui.theme.DarkenedYellow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

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

data class WordState(
    val fixed: Boolean,
    val tileStates: List<TileState>
)

@Immutable
data class HomeViewState(
    val wordStates: List<WordState>
)

class HomeViewModel @Inject constructor() : ViewModel() {

    private val word = "party"
    private val maxGuesses = 6
    private val wordSize = 5

    val state: StateFlow<HomeViewState> = MutableStateFlow(createInitialState(maxGuesses, wordSize))

    /*val state: StateFlow<HomeViewState> = combine(
        observeSubmission.flow,
        loadingState.observable,
    ) { submissionAndComments: SubmissionAndComments?, refreshing ->

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = createInitialState(maxGuesses, wordSize)
    )*/

    fun onCharEntered(char: Char) {

    }

    private fun createInitialState(maxGuesses: Int, wordSize: Int): HomeViewState {
        val tileStates = mutableListOf<TileState>()
        for (t in 0 until wordSize) {
            tileStates.add(TileState.Empty)
        }

        val wordStates = mutableListOf<WordState>()
        val defaultWordState = WordState(false, tileStates)
        for (w in 0 until maxGuesses) {
            wordStates.add(defaultWordState)
        }

        return HomeViewState(wordStates)
    }

}