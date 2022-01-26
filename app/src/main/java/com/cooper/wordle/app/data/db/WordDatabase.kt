package com.cooper.wordle.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FourLetterWordEntity::class,
        FiveLetterWordEntity::class,
        SixLetterWordEntity::class,
        SevenLetterWordEntity::class,
    ],
    version = 1,
    exportSchema = false
)
internal abstract class WordDatabase : RoomDatabase() {

    internal abstract fun wordDao(): WordDao
}