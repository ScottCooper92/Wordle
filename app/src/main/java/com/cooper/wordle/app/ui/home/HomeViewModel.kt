package com.cooper.wordle.app.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cooper.wordle.app.ui.components.Key
import com.cooper.wordle.app.ui.theme.ColorTone3
import com.cooper.wordle.app.ui.theme.ColorTone4
import com.cooper.wordle.app.ui.theme.DarkGreen
import com.cooper.wordle.app.ui.theme.DarkenedYellow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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

data class WordState(val tileStates: List<TileState>) {
    val letterCount: Int
        get() = tileStates.count { it !is TileState.Empty }
}

@Immutable
data class HomeViewState(
    internal val completeWords: List<WordState>,
    internal val currentWord: WordState?,
    internal val remainingGuesses: List<WordState>,
) {

    val wordStates: List<WordState>
        get() {
            return mutableListOf<WordState>().apply {
                addAll(completeWords)
                currentWord?.let { add(it) }
                addAll(remainingGuesses)
            }
        }


    companion object {
        internal fun initialState(maxGuesses: Int, wordSize: Int): HomeViewState {
            val emptyWord = List(wordSize) { TileState.Empty }
            return HomeViewState(
                completeWords = emptyList(),
                currentWord = WordState(emptyWord),
                remainingGuesses = List(maxGuesses - 1) { WordState(emptyWord) }
            )
        }
    }
}

class HomeViewModel @Inject constructor() : ViewModel() {

    private val word = "party"
    private val maxGuesses = 6
    private val wordSize = 5

    val _state = MutableStateFlow(HomeViewState.initialState(maxGuesses, wordSize))
    val state: StateFlow<HomeViewState> = _state

    fun onKeyClicked(key: Key) {
        when (key) {
            is Key.IconKey -> onIconKeyClicked(key)
            is Key.CharKey -> onCharKeyClicked(key)
            is Key.StringKey -> onStringKeyClicked(key)
        }
    }

    private fun onCharKeyClicked(key: Key.CharKey) {
        viewModelScope.launch {
            val currentState = state.value
            val currentWord =
                currentState.currentWord ?: throw IllegalStateException("Current word is null")

            if (currentWord.letterCount >= wordSize) {
                Timber.d("Word is full")
                //TODO - Should we show a prompt to the user?
                return@launch
            }

            val newTile = TileState.Foo(key.char)
            val tiles = currentWord.tileStates.toMutableList().apply {
                val index = indexOfFirst { it is TileState.Empty }
                set(index, newTile)
            }

            val newWord = WordState(tiles)
            val newState = currentState.copy(currentWord = newWord)

            _state.emit(newState)
        }
    }

    private fun onStringKeyClicked(key: Key.StringKey) {
        viewModelScope.launch {
            val currentState = state.value
            val currentWord =
                currentState.currentWord ?: throw IllegalStateException("Current word is null")

            if (currentWord.letterCount != wordSize) {
                Timber.d("Word isn't complete")
                //TODO - Should we show a prompt to the user?
                return@launch
            }

            // mark the current word as complete
            val completeWords = currentState.completeWords.toMutableList().apply {
                add(currentWord)
            }

            val remainingGuesses = currentState.remainingGuesses.toMutableList()

            // if we have no remaining guesses then the game is over
            val newWord = if (remainingGuesses.isNotEmpty()) {
                remainingGuesses.removeAt(0)
            } else null

            //TODO - Handle game over

            val newState = currentState.copy(
                completeWords = completeWords,
                currentWord = newWord,
                remainingGuesses = remainingGuesses
            )

            _state.emit(newState)
        }
    }

    private fun onIconKeyClicked(key: Key.IconKey) {
        viewModelScope.launch {
            val currentState = state.value
            val currentWord =
                currentState.currentWord ?: throw IllegalStateException("Current word is null")

            if (currentWord.letterCount <= 0) {
                Timber.d("Word is empty")
                //TODO - Should we show a prompt to the user?
                return@launch
            }

            val tiles = currentWord.tileStates.toMutableList().apply {
                val index = indexOfLast { it is TileState.Foo }
                set(index, TileState.Empty)
            }

            val newWord = WordState(tiles)
            val newState = currentState.copy(currentWord = newWord)

            _state.emit(newState)
        }
    }
}