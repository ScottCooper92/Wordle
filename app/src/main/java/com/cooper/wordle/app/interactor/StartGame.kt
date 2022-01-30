package com.cooper.wordle.app.interactor

import com.cooper.wordle.app.interactor.StartGame.Params
import com.cooper.wordle.game.GameManager
import com.cooper.wordle.game.data.Letters
import javax.inject.Inject

class StartGame @Inject constructor(private val gameManager: GameManager) : Interactor<Params>() {

    override suspend fun doWork(params: Params) {
        gameManager.newGame(params.letters)
    }

    data class Params(val letters: Letters)
}