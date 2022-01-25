package com.cooper.wordle.app.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

//TODO - Inject the dispatcher
class WordStore @Inject constructor(@ApplicationContext private val context: Context) {

    fun randomWord(letters: Letters): String {
        return context.assets.open(letters.filename)
            .bufferedReader()
            .useLines {
                it.random()
            }
    }

    enum class Letters(val filename: String) {
        FOUR("4letterwords.txt"),
        FIVE("5letterwords.txt"),
        SIX("6letterwords.txt"),
        SEVEN("7letterwords.txt")
    }
}

/**
 * Simple function to choose a random int between the start and end of a sequence and return
 * the item at that index.
 */
fun <T> Sequence<T>.random(): T = withIndex().last {
    Random.nextInt(0..it.index) == 0
}.value