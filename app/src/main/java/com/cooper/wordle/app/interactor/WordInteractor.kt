package com.cooper.wordle.app.interactor

import com.cooper.wordle.app.data.WordStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordInteractor @Inject constructor(
    private val wordStore: WordStore
) :
    SuspendingWorkInteractor<WordInteractor.Params, String>() {

    override suspend fun doWork(params: Params): String {
        return withContext(Dispatchers.IO) {
            wordStore.randomWord(params.numLetters)
        }
    }

    data class Params(val numLetters: WordStore.Letters)
}