package com.cooper.wordle.app.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class WordDao {

    @Query("""SELECT * FROM four_letters ORDER BY RANDOM()""")
    abstract suspend fun getRandom4LetterWord(): FourLetterWordEntity

    @Query("""SELECT COUNT(1) FROM four_letters WHERE word = :word""")
    abstract suspend fun validate4LetterWord(word: String): Int

    @Query("""SELECT * FROM five_letters ORDER BY RANDOM()""")
    abstract suspend fun getRandom5LetterWord(): FiveLetterWordEntity

    @Query("""SELECT COUNT(1) FROM five_letters WHERE word = :word""")
    abstract suspend fun validate5LetterWord(word: String): Int

    @Query("""SELECT * FROM six_letters ORDER BY RANDOM()""")
    abstract suspend fun getRandom6LetterWord(): SixLetterWordEntity

    @Query("""SELECT COUNT(1) FROM six_letters WHERE word = :word""")
    abstract suspend fun validate6LetterWord(word: String): Int

    @Query("""SELECT * FROM seven_letters ORDER BY RANDOM()""")
    abstract suspend fun getRandom7LetterWord(): SevenLetterWordEntity

    @Query("""SELECT COUNT(1) FROM seven_letters WHERE word = :word""")
    abstract suspend fun validate7LetterWord(word: String): Int
}