package com.cooper.wordle.game.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
internal data class WordEntity(
    @PrimaryKey
    val word: String
)