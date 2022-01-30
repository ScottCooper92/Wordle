package com.cooper.wordle.app.interactor

import com.cooper.wordle.game.GameManager
import javax.inject.Inject

class AddLetter @Inject constructor(
    private val gameManager: GameManager
) : Interactor<AddLetter.Params>() {

    override suspend fun doWork(params: Params) {
        gameManager.addLetter(params.letter)
    }

    data class Params(val letter: Char)
}