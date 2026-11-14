package com.playwithme.assistant.domain.usecase

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.domain.model.HeroRole
import com.playwithme.assistant.domain.repository.HeroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case: Get a single hero by ID.
 */
class GetHeroByIdUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    suspend operator fun invoke(heroId: String): Result<Hero> =
        repository.getHeroById(heroId)
}

/**
 * Use case: Get heroes filtered by role.
 */
class GetHeroesByRoleUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    operator fun invoke(role: HeroRole): Flow<List<Hero>> =
        repository.getHeroesByRole(role)
}

/**
 * Use case: Get all heroes as a Flow.
 */
class GetAllHeroesUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    operator fun invoke(): Flow<List<Hero>> = repository.getAllHeroes()
}

/**
 * Use case: Search heroes by name or title.
 *
 * Returns an empty list for blank queries instead of throwing.
 */
class SearchHeroUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    operator fun invoke(query: String): Flow<List<Hero>> {
        if (query.isBlank()) return repository.getAllHeroes()
        return repository.searchHeroes(query.trim())
    }
}

/**
 * Use case: Get counter heroes for a given hero.
 */
class GetCounterHeroUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    suspend operator fun invoke(heroId: String): Result<List<Hero>> =
        repository.getCounterHeroes(heroId)
}

/**
 * Use case: Get heroes that a given hero counters (strong against).
 */
class GetStrongAgainstHeroesUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    suspend operator fun invoke(heroId: String): Result<List<Hero>> =
        repository.getStrongAgainstHeroes(heroId)
}

/**
 * Use case: Sync heroes from remote API.
 */
class SyncHeroesUseCase @Inject constructor(
    private val repository: HeroRepository
) {
    suspend operator fun invoke(): Result<Int> = repository.syncHeroesFromRemote()
}
