package com.cooper.wordle.app.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cooper.wordle.app.ui.components.Key
import com.cooper.wordle.app.ui.home.GuessChecker.checkGuess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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

    private val tempAnswer = "happy"
    private val maxGuesses = 6
    private val wordSize = 5

    private val _state = MutableStateFlow(HomeViewState.initialState(maxGuesses, wordSize))
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

            // check if the word is complete
            if (currentWord.letterCount != wordSize) {
                Timber.d("Word isn't complete")
                //TODO - Should we show a prompt to the user?
                return@launch
            }

            //TODO - check if it's in the dictionary

            // check it against the actual word
            val completeWord = checkGuess(currentWord, tempAnswer)

            // mark the current word as complete
            val completeWords = currentState.completeWords.toMutableList().apply {
                add(completeWord)
            }

            // if the complete word is fully correct the the game is over
            //TODO - Handle game over

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