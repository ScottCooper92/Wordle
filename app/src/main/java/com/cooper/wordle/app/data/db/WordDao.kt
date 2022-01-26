package com.cooper.wordle.app.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class WordDao {

    @Query("""SELECT * FROM words WHERE LENGTH(word) = :numLetters ORDER BY RANDOM()""")
    abstract suspend fun getRandomWord(numLetters: Int): WordEntity

    @Query("""SELECT COUNT(1) FROM words WHERE word = :word""")
    abstract suspend fun validateWord(word: String): Int
}