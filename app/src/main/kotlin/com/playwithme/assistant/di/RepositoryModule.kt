package com.playwithme.assistant.di

import com.playwithme.assistant.data.repository.HeroRepositoryImpl
import com.playwithme.assistant.data.repository.NoteRepositoryImpl
import com.playwithme.assistant.data.repository.SettingsRepositoryImpl
import com.playwithme.assistant.domain.repository.HeroRepository
import com.playwithme.assistant.domain.repository.NoteRepository
import com.playwithme.assistant.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to their implementations.
 *
 * Using @Binds (not @Provides) for interface-to-impl binding is more efficient
 * — Hilt generates less code and avoids wrapping in a Dagger module method.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHeroRepository(impl: HeroRepositoryImpl): HeroRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
