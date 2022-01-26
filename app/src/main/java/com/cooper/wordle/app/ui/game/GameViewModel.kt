package com.cooper.wordle.app.ui.game

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cooper.wordle.app.data.WordStore
import com.cooper.wordle.app.interactor.*
import com.cooper.wordle.app.ui.components.Key
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed class GameViewState {
    object Loading : GameViewState()

    data class InProgress(
        val solution: String,
        val gridState: GridState,
    ) : GameViewState()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val startGame: StartGame,
    private val addLetter: AddLetter,
    private val removeLetter: RemoveLetter,
    private val evaluateWord: EvaluateWord,
    observeGameState: ObserveGameState,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val wordLength = savedStateHandle.get<WordStore.Letters>("letters")!!
    val state: StateFlow<GameViewState> = observeGameState.flow.mapLatest {
        when (it) {
            GameState.Uninitialised -> GameViewState.Loading
            is GameState.InProgress -> GameViewState.InProgress(it.solution, it.gridState)
            is GameState.Won -> GameViewState.InProgress(it.solution, it.gridState)
            is GameState.Lost -> GameViewState.InProgress(it.solution, it.gridState)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        GameViewState.Loading
    )

    init {
        viewModelScope.launch {
            observeGameState(ObserveGameState.Params())
        }
        viewModelScope.launch {
            startGame.executeSync(StartGame.Params(wordLength))
        }
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
            addLetter.executeSync(AddLetter.Params(key.char))
        }
    }

    private fun onIconKeyClicked(key: Key.IconKey) {
        viewModelScope.launch {
            removeLetter.executeSync(Unit)
        }
    }

    private fun onStringKeyClicked(key: Key.StringKey) {
        viewModelScope.launch {
            evaluateWord.executeSync(Unit)
        }
    }
}