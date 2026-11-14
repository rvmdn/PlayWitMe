package com.playwithme.assistant.data.seeding

import android.content.Context
import com.playwithme.assistant.data.local.dao.HeroDao
import com.playwithme.assistant.data.local.entity.HeroEntity
import com.playwithme.assistant.data.local.mapper.toEntity
import com.playwithme.assistant.data.remote.dto.HeroRemoteDto
import com.playwithme.assistant.domain.model.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the local database with hero data from [assets/hero_data.json].
 *
 * Called once on first launch (when DB is empty) via [AppInitializer].
 * This ensures the app works offline from the first install.
 */
@Singleton
class HeroSeedDataLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val heroDao: HeroDao,
    private val moshi: Moshi
) {

    /** Load seed data into the database if it's empty. */
    suspend fun seedIfEmpty(): Int {
        val existingCount = heroDao.getHeroCount()
        if (existingCount > 0) {
            Timber.d("HeroSeedDataLoader: DB already has $existingCount heroes, skipping seed")
            return 0
        }

        return try {
            val entities = loadEntitiesFromAssets()
            heroDao.insertHeroes(entities)
            Timber.d("HeroSeedDataLoader: seeded ${entities.size} heroes")
            entities.size
        } catch (e: Exception) {
            Timber.e(e, "HeroSeedDataLoader: failed to seed hero data")
            0
        }
    }

    private fun loadEntitiesFromAssets(): List<HeroEntity> {
        val inputStream = context.assets.open("hero_data.json")
        val reader = InputStreamReader(inputStream)
        val json = reader.readText()
        reader.close()

        val listType = Types.newParameterizedType(List::class.java, HeroRemoteDto::class.java)
        val adapter: JsonAdapter<List<HeroRemoteDto>> = moshi.adapter(listType)
        val remoteDtos: List<HeroRemoteDto> = adapter.fromJson(json) ?: emptyList()
        Timber.d("HeroSeedDataLoader: loaded ${remoteDtos.size} heroes from assets")

        return remoteDtos.map { dto -> mapDtoToEntity(dto) }
    }

    private fun mapDtoToEntity(dto: HeroRemoteDto): HeroEntity {
        val domain = Hero(
            id = dto.id,
            name = dto.name,
            title = dto.title,
            role = dto.roles.mapNotNull { runCatching { HeroRole.valueOf(it) }.getOrNull() },
            lane = dto.lanes.mapNotNull { runCatching { HeroLane.valueOf(it) }.getOrNull() },
            difficulty = runCatching { HeroDifficulty.valueOf(dto.difficulty) }.getOrDefault(HeroDifficulty.MODERATE),
            description = dto.description,
            iconUrl = dto.iconUrl,
            skills = dto.skills.map { s ->
                HeroSkill(
                    key = runCatching { SkillKey.valueOf(s.key) }.getOrDefault(SkillKey.PASSIVE),
                    name = s.name,
                    description = s.description,
                    cooldown = s.cooldown,
                    manaCost = s.manaCost,
                    type = runCatching { SkillType.valueOf(s.type) }.getOrDefault(SkillType.ACTIVE)
                )
            },
            recommendedBuilds = dto.builds.map { b ->
                HeroBuild(
                    id = b.id,
                    name = b.name,
                    phase = runCatching { BuildPhase.valueOf(b.phase) }.getOrDefault(BuildPhase.MID),
                    items = b.items.map { i ->
                        BuildItem(id = i.id, name = i.name, price = i.price, iconUrl = i.iconUrl, description = i.description)
                    },
                    description = b.description
                )
            },
            recommendedEmblem = dto.emblem?.let { e ->
                HeroEmblem(name = e.name, tier = e.tier, talents = e.talents)
            } ?: HeroEmblem("Default", 1, emptyList()),
            recommendedSpells = dto.spells,
            counters = dto.counters,
            strongAgainst = dto.strongAgainst,
            tips = dto.tips,
            combos = dto.combos.map { c ->
                HeroCombo(
                    id = c.id,
                    name = c.name,
                    description = c.description,
                    steps = c.steps.map { s ->
                        ComboStep(
                            order = s.order,
                            skillKey = runCatching { SkillKey.valueOf(s.skillKey) }.getOrDefault(SkillKey.SKILL_1),
                            description = s.description,
                            timing = s.timing
                        )
                    },
                    difficulty = runCatching { HeroDifficulty.valueOf(c.difficulty) }.getOrDefault(HeroDifficulty.MODERATE),
                    notes = c.notes
                )
            }
        )
        return domain.toEntity()
    }
}
