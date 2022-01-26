package com.cooper.wordle.app.interactor

import com.cooper.wordle.app.data.GameManager
import com.cooper.wordle.app.data.WordStore
import com.cooper.wordle.app.interactor.StartGame.Params
import javax.inject.Inject

class StartGame @Inject constructor(
    private val gameManager: GameManager
) : Interactor<Params>() {

    override suspend fun doWork(params: Params) {
        gameManager.newGame(params.letters)
    }

    data class Params(val letters: WordStore.Letters)
}