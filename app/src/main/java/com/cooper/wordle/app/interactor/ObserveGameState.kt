package com.cooper.wordle.app.interactor

import com.cooper.wordle.app.data.GameManager
import com.cooper.wordle.app.data.GameState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGameState @Inject constructor(
    private val gameManager: GameManager
) : FlowInteractor<ObserveGameState.Params, GameState>() {

    override fun createFlow(params: Params): Flow<GameState> = gameManager.state

    class Params
}