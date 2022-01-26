package com.cooper.wordle.app.data

import com.cooper.wordle.app.data.db.WordDao
import javax.inject.Inject

class WordStore @Inject constructor(private val wordDao: WordDao) {

    suspend fun randomWord(letters: Letters): String {
        return when (letters) {
            Letters.FOUR -> wordDao.getRandom4LetterWord()
            Letters.FIVE -> wordDao.getRandom5LetterWord()
            Letters.SIX -> wordDao.getRandom6LetterWord()
            Letters.SEVEN -> wordDao.getRandom7LetterWord()
        }.word
    }

    suspend fun wordExists(word: String): Boolean {
        return when (word.count()) {
            4 -> wordDao.validate4LetterWord(word)
            5 -> wordDao.validate5LetterWord(word)
            6 -> wordDao.validate6LetterWord(word)
            7 -> wordDao.validate7LetterWord(word)
            else -> throw IllegalArgumentException("Word $word must be between 4-5 chars long")
        } > 0
    }

    enum class Letters(val size: Int) {
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7)
    }
}