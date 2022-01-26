package com.cooper.wordle.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val word: String
)