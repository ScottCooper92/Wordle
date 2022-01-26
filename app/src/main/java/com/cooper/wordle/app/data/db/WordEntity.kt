package com.cooper.wordle.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

internal interface WordEntity {
    val word: String
}

@Entity(tableName = "four_letters")
data class FourLetterWordEntity(
    @PrimaryKey
    override val word: String
) : WordEntity

@Entity(tableName = "five_letters")
data class FiveLetterWordEntity(
    @PrimaryKey
    override val word: String
) : WordEntity

@Entity(tableName = "six_letters")
data class SixLetterWordEntity(
    @PrimaryKey
    override val word: String
) : WordEntity

@Entity(tableName = "seven_letters")
data class SevenLetterWordEntity(
    @PrimaryKey
    override val word: String
) : WordEntity
