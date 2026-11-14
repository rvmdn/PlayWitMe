package com.playwithme.assistant.data.repository

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.core.common.safeCall
import com.playwithme.assistant.data.local.dao.HeroDao
import com.playwithme.assistant.data.local.mapper.toDomain
import com.playwithme.assistant.data.local.mapper.toEntity
import com.playwithme.assistant.data.remote.datasource.HeroRemoteDataSource
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.domain.model.HeroRole
import com.playwithme.assistant.domain.repository.HeroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [HeroRepository].
 *
 * Strategy: Local-first. Remote data synced via WorkManager, not real-time.
 */
@Singleton
class HeroRepositoryImpl @Inject constructor(
    private val heroDao: HeroDao,
    private val remoteDataSource: HeroRemoteDataSource
) : HeroRepository {

    override fun getAllHeroes(): Flow<List<Hero>> =
        heroDao.getAllHeroes().map { entities -> entities.map { it.toDomain() } }

    override fun getHeroesByRole(role: HeroRole): Flow<List<Hero>> =
        heroDao.getHeroesByRole(role.name).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getHeroById(id: String): Result<Hero> = safeCall {
        heroDao.getHeroById(id)?.toDomain()
            ?: throw NoSuchElementException("Hero not found: $id")
    }

    override fun getScannedHeroes(): Flow<List<Hero>> =
        heroDao.getScannedHeroes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertHero(hero: Hero) {
        heroDao.insertHero(hero.toEntity())
    }

    override fun searchHeroes(query: String): Flow<List<Hero>> =
        heroDao.searchHeroes(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCounterHeroes(heroId: String): Result<List<Hero>> = safeCall {
        val hero = heroDao.getHeroById(heroId)?.toDomain()
            ?: throw NoSuchElementException("Hero not found: $heroId")
        hero.counters.mapNotNull { counterId ->
            heroDao.getHeroById(counterId)?.toDomain()
        }
    }

    override suspend fun getStrongAgainstHeroes(heroId: String): Result<List<Hero>> = safeCall {
        val hero = heroDao.getHeroById(heroId)?.toDomain()
            ?: throw NoSuchElementException("Hero not found: $heroId")
        hero.strongAgainst.mapNotNull { id ->
            heroDao.getHeroById(id)?.toDomain()
        }
    }

    override suspend fun syncHeroesFromRemote(): Result<Int> = safeCall {
        val remoteHeroes = remoteDataSource.fetchAllHeroes() // Already List<Hero>
        val entities = remoteHeroes.map { hero: Hero -> hero.toEntity() }
        heroDao.insertHeroes(entities)
        Timber.d("Synced ${entities.size} heroes from remote")
        entities.size
    }

    override suspend fun isHeroDataAvailable(): Boolean =
        heroDao.getHeroCount() > 0
}
