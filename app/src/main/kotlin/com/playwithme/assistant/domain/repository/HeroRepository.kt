package com.playwithme.assistant.domain.repository

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.domain.model.HeroRole
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for hero data.
 *
 * The domain layer only knows about this interface.
 * The data layer provides the implementation via Hilt.
 */
interface HeroRepository {

    /** Observe all heroes as a live Flow. */
    fun getAllHeroes(): Flow<List<Hero>>

    /** Observe heroes filtered by [role]. */
    fun getHeroesByRole(role: HeroRole): Flow<List<Hero>>

    /** Get a single hero by [id]. */
    suspend fun getHeroById(id: String): Result<Hero>

    /** Get heroes that were added via scanner. */
    fun getScannedHeroes(): Flow<List<Hero>>

    /** Insert a single hero into the database. */
    suspend fun insertHero(hero: Hero)

    /** Search heroes by [query] (name or title). */
    fun searchHeroes(query: String): Flow<List<Hero>>

    /** Get counter heroes for a given hero [id]. */
    suspend fun getCounterHeroes(heroId: String): Result<List<Hero>>

    /** Get heroes that a given hero [id] counters. */
    suspend fun getStrongAgainstHeroes(heroId: String): Result<List<Hero>>

    /** Sync hero data from remote source. Returns number of heroes updated. */
    suspend fun syncHeroesFromRemote(): Result<Int>

    /** Returns true if local hero data is populated. */
    suspend fun isHeroDataAvailable(): Boolean
}
