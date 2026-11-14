package com.playwithme.assistant.data.remote.datasource

import com.playwithme.assistant.data.remote.ApiService
import com.playwithme.assistant.data.remote.dto.HeroRemoteDto
import com.playwithme.assistant.data.local.mapper.*
import com.playwithme.assistant.domain.model.Hero
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for hero data.
 *
 * Bridges Retrofit API calls to domain-agnostic DTOs.
 * Maps DTOs → domain models before returning.
 */
@Singleton
class HeroRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Fetches all heroes from the remote API.
     *
     * @return List of [Hero] domain models.
     * @throws Exception on network or parsing failure.
     */
    suspend fun fetchAllHeroes(): List<Hero> {
        return try {
            apiService.getAllHeroes().map { dto -> dto.toDomain() }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch heroes from remote")
            throw e
        }
    }

    suspend fun fetchHeroById(id: String): Hero {
        return apiService.getHeroById(id).toDomain()
    }
}

// ─── Remote DTO → Domain mappers ──────────────────────────────────────────────
// These mappers are internal to the data layer — not in domain

private fun HeroRemoteDto.toDomain(): Hero = Hero(
    id = id,
    name = name,
    title = title,
    role = roles.mapNotNull { runCatching { com.playwithme.assistant.domain.model.HeroRole.valueOf(it) }.getOrNull() },
    lane = lanes.mapNotNull { runCatching { com.playwithme.assistant.domain.model.HeroLane.valueOf(it) }.getOrNull() },
    difficulty = runCatching { com.playwithme.assistant.domain.model.HeroDifficulty.valueOf(difficulty) }.getOrDefault(com.playwithme.assistant.domain.model.HeroDifficulty.MODERATE),
    description = description,
    iconUrl = iconUrl,
    skills = skills.map { s ->
        com.playwithme.assistant.domain.model.HeroSkill(
            key = runCatching { com.playwithme.assistant.domain.model.SkillKey.valueOf(s.key) }.getOrDefault(com.playwithme.assistant.domain.model.SkillKey.PASSIVE),
            name = s.name, description = s.description, cooldown = s.cooldown, manaCost = s.manaCost,
            type = runCatching { com.playwithme.assistant.domain.model.SkillType.valueOf(s.type) }.getOrDefault(com.playwithme.assistant.domain.model.SkillType.ACTIVE),
            iconUrl = s.iconUrl
        )
    },
    recommendedBuilds = builds.map { b ->
        com.playwithme.assistant.domain.model.HeroBuild(
            id = b.id, name = b.name,
            phase = runCatching { com.playwithme.assistant.domain.model.BuildPhase.valueOf(b.phase) }.getOrDefault(com.playwithme.assistant.domain.model.BuildPhase.MID),
            items = b.items.map { i -> com.playwithme.assistant.domain.model.BuildItem(id = i.id, name = i.name, price = i.price, iconUrl = i.iconUrl, description = i.description) },
            description = b.description,
            playstyle = runCatching { com.playwithme.assistant.domain.model.BuildPlaystyle.valueOf(b.playstyle) }.getOrDefault(com.playwithme.assistant.domain.model.BuildPlaystyle.STANDARD)
        )
    },
    recommendedEmblem = emblem?.let {
        com.playwithme.assistant.domain.model.HeroEmblem(name = it.name, tier = it.tier, talents = it.talents, description = it.description)
    } ?: com.playwithme.assistant.domain.model.HeroEmblem("Default", 1, emptyList()),
    recommendedSpells = spells,
    counters = counters,
    strongAgainst = strongAgainst,
    tips = tips,
    combos = combos.map { c ->
        com.playwithme.assistant.domain.model.HeroCombo(
            id = c.id, name = c.name, description = c.description,
            steps = c.steps.map { s ->
                com.playwithme.assistant.domain.model.ComboStep(
                    order = s.order,
                    skillKey = runCatching { com.playwithme.assistant.domain.model.SkillKey.valueOf(s.skillKey) }.getOrDefault(com.playwithme.assistant.domain.model.SkillKey.SKILL_1),
                    description = s.description, timing = s.timing
                )
            },
            difficulty = runCatching { com.playwithme.assistant.domain.model.HeroDifficulty.valueOf(c.difficulty) }.getOrDefault(com.playwithme.assistant.domain.model.HeroDifficulty.MODERATE),
            notes = c.notes
        )
    }
)
