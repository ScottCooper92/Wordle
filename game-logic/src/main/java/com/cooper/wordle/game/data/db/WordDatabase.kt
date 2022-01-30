package com.cooper.wordle.game.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        WordEntity::class,
    ],
    version = 1,
    exportSchema = false
)
internal abstract class WordDatabase : RoomDatabase() {

    internal abstract fun wordDao(): WordDao
}