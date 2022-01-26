package com.cooper.wordle.app.data

import com.cooper.wordle.app.data.db.WordDao
import javax.inject.Inject

class WordStore @Inject constructor(private val wordDao: WordDao) {

    suspend fun randomWord(letters: Letters): String {
        return wordDao.getRandomWord(letters.size).word
    }

    suspend fun wordExists(word: String): Boolean {
        return wordDao.validateWord(word) > 0
    }

    enum class Letters(val size: Int) {
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7)
    }
}