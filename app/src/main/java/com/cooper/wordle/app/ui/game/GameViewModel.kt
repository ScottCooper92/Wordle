package com.cooper.wordle.app.ui.game

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cooper.wordle.app.interactor.*
import com.cooper.wordle.app.ui.common.SnackbarManager
import com.cooper.wordle.app.ui.common.UiMessage
import com.cooper.wordle.app.ui.game.components.Key
import com.cooper.wordle.game.EvaluatedLetterState
import com.cooper.wordle.game.GridState
import com.cooper.wordle.game.IncorrectGuess
import com.cooper.wordle.game.InvalidGuess
import com.cooper.wordle.game.data.Letters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed class GameViewState {
    object Loading : GameViewState()

    data class InProgress(
        val solution: String,
        val usedLetters: Set<EvaluatedLetterState>,
        val gridState: GridState
    ) : GameViewState()
}

sealed class GameUiEffect {
    data class ShowMessage(val message: String) : GameUiEffect()
    object ClearMessage : GameUiEffect()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val startGame: StartGame,
    private val addLetter: AddLetter,
    private val removeLetter: RemoveLetter,
    private val evaluateWord: EvaluateWord,
    private val snackbarManager: SnackbarManager,
    observeGameState: ObserveGameState,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val wordLength = savedStateHandle.get<Letters>("letters")!!
    val state: StateFlow<GameViewState> = observeGameState.flow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        GameViewState.Loading
    )

    private val _effects = MutableSharedFlow<GameUiEffect>()
    val effects: Flow<GameUiEffect>
        get() = _effects

    init {
        viewModelScope.launch {
            observeGameState(null)
        }
        viewModelScope.launch {
            startGame.executeSync(StartGame.Params(wordLength))
        }

        viewModelScope.launch {
            snackbarManager.messages.collect {
                when {
                    it != null -> _effects.emit(GameUiEffect.ShowMessage(it.message))
                    else -> _effects.emit(GameUiEffect.ClearMessage)
                }
            }
        }
    }

    fun onKeyClicked(key: Key) {
        when (key) {
            is Key.IconKey -> onIconKeyClicked(key)
            is Key.CharKey -> onCharKeyClicked(key)
        }
    }

    private fun onCharKeyClicked(key: Key.CharKey) {
        viewModelScope.launch {
            addLetter.executeSync(AddLetter.Params(key.char))
        }
    }

    private fun onIconKeyClicked(key: Key.IconKey) {
        viewModelScope.launch {
            when (key) {
                Key.DELETE -> removeLetter.executeSync(Unit)
                Key.SUBMIT -> evaluateWord()
            }
        }
    }

    private fun evaluateWord() {
        viewModelScope.launch {
            evaluateWord(Unit).collectLatest { invokeStatus ->
                if (invokeStatus is InvokeError) {
                    when (invokeStatus.throwable) {
                        is InvalidGuess -> {
                            snackbarManager.addMessage(UiMessage("Not enough letters"))
                        }
                        is IncorrectGuess -> {
                            snackbarManager.addMessage(UiMessage("Not in word list"))
                        }
                    }
                }
            }
        }
    }
}