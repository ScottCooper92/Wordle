package com.cooper.wordle.app

import android.content.Context
import androidx.room.Room
import com.cooper.wordle.app.data.db.WordDao
import com.cooper.wordle.app.data.db.WordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WordDatabase {
        return Room.databaseBuilder(context, WordDatabase::class.java, "words.db")
            .createFromAsset("words.db").build()
    }

    @Provides
    fun provideWordDao(wordDatabase: WordDatabase): WordDao {
        return wordDatabase.wordDao()
    }

}