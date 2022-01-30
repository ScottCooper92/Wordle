package com.cooper.wordle.game.data

import com.cooper.wordle.game.data.db.WordDao
import javax.inject.Inject

internal class WordStore @Inject constructor(private val wordDao: WordDao) {

    suspend fun randomWord(letters: Letters): String {
        return wordDao.getRandomWord(letters.size).word
    }

    suspend fun wordExists(word: String): Boolean {
        return wordDao.validateWord(word) > 0
    }
}

enum class Letters(val size: Int) {
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7)
}