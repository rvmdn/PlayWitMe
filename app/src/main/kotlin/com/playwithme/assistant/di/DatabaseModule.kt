package com.playwithme.assistant.di

import android.content.Context
import androidx.room.Room
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.data.local.AppDatabase
import com.playwithme.assistant.data.local.dao.HeroDao
import com.playwithme.assistant.data.local.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration() // TODO: Replace with proper migrations in production
            .build()

    @Provides
    @Singleton
    fun provideHeroDao(db: AppDatabase): HeroDao = db.heroDao()

    @Provides
    @Singleton
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
}
