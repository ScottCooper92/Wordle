package com.cooper.wordle.app.interactor

import com.cooper.wordle.game.GameManager
import javax.inject.Inject

class EvaluateWord @Inject constructor(
    private val gameManager: GameManager
) : Interactor<Unit>() {

    override suspend fun doWork(params: Unit) {
        gameManager.evaluateCurrentRow()
    }
}