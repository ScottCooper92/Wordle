package com.cooper.wordle.app.ui.game

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cooper.wordle.app.data.WordStore
import com.cooper.wordle.app.interactor.WordInteractor
import com.cooper.wordle.app.ui.components.Key
import com.cooper.wordle.app.ui.game.GuessChecker.checkGuess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
data class GameViewState(
    val answer: String,
    val maxGuesses: Int = 6,
    val completeWords: List<WordState> = emptyList(),
    val currentWord: WordState? = WordState.emptyWord(answer.length),
    val remainingGuesses: List<WordState> = List(maxGuesses - 1) { WordState.emptyWord(answer.length) }
) {
    val wordLength: Int
        get() = answer.length

    val wordStates: List<WordState>
        get() {
            return mutableListOf<WordState>().apply {
                addAll(completeWords)
                currentWord?.let { add(it) }
                addAll(remainingGuesses)
            }
        }
}

@HiltViewModel
class GameViewModel @Inject constructor(
    wordInteractor: WordInteractor,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val maxGuesses = 6
    private val initialWord = savedStateHandle.get<String>("word") ?: "happy"
    private val wordLength =
        savedStateHandle.get<WordStore.Letters>("letters") ?: WordStore.Letters.FIVE

    private val initialState: Flow<GameViewState> = wordInteractor.flow.map { answer ->
        GameViewState(answer, maxGuesses)
    }
    private val updatedState = MutableSharedFlow<GameViewState>()

    val state: StateFlow<GameViewState> = combine(
        initialState,
        updatedState
    ) { initialState, updatedState ->
        // if the answers are different then prefer the initialState
        if (initialState.answer != updatedState.answer) initialState
        else updatedState
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GameViewState(initialWord)
    )

    init {
        wordInteractor(WordInteractor.Params(wordLength))
    }

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

            if (currentWord.letterCount >= currentState.answer.length) {
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

            updatedState.emit(newState)
        }
    }

    private fun onStringKeyClicked(key: Key.StringKey) {
        viewModelScope.launch {
            val currentState = state.value
            val currentWord =
                currentState.currentWord ?: throw IllegalStateException("Current word is null")

            // check if the word is complete
            if (currentWord.letterCount != currentState.wordLength) {
                Timber.d("Word isn't complete")
                //TODO - Should we show a prompt to the user?
                return@launch
            }

            //TODO - check if it's in the dictionary

            // check it against the actual word
            val completeWord = checkGuess(currentWord, currentState.answer)

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

            updatedState.emit(newState)
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

            updatedState.emit(newState)
        }
    }
}