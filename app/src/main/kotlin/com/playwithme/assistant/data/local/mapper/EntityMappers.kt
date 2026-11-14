package com.playwithme.assistant.data.local.mapper

import com.playwithme.assistant.data.local.entity.HeroEntity
import com.playwithme.assistant.data.local.entity.NoteEntity
import com.playwithme.assistant.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Shared Json instance
private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

// ─── Hero Mapper ──────────────────────────────────────────────────────────────

fun HeroEntity.toDomain(): Hero = Hero(
    id = id,
    mlid = mlid,
    uid = uid,
    name = name,
    title = title,
    discordmoji = discordmoji,
    releaseYear = releaseYear,
    heroClass = heroClass,
    role = roles,
    lane = lanes,
    difficulty = difficulty,
    description = description,
    iconUrl = iconUrl,
    portrait = portrait,
    skills = runCatching { json.decodeFromString<List<HeroSkillDto>>(skillsJson) }
        .getOrDefault(emptyList())
        .map { it.toDomain() },
    recommendedBuilds = runCatching { json.decodeFromString<List<HeroBuildDto>>(buildsJson) }
        .getOrDefault(emptyList())
        .map { it.toDomain() },
    recommendedEmblem = runCatching { json.decodeFromString<HeroEmblemDto>(emblemJson) }
        .getOrElse { HeroEmblemDto("Default", 1, emptyList()) }
        .toDomain(),
    recommendedSpells = runCatching { json.decodeFromString<List<String>>(spellsJson) }
        .getOrDefault(emptyList()),
    counters = runCatching { json.decodeFromString<List<String>>(countersJson) }
        .getOrDefault(emptyList()),
    strongAgainst = runCatching { json.decodeFromString<List<String>>(strongAgainstJson) }
        .getOrDefault(emptyList()),
    tips = runCatching { json.decodeFromString<List<String>>(tipsJson) }
        .getOrDefault(emptyList()),
    combos = runCatching { json.decodeFromString<List<HeroComboDto>>(combosJson) }
        .getOrDefault(emptyList())
        .map { it.toDomain() },
)

fun Hero.toEntity(): HeroEntity = HeroEntity(
    id = id,
    mlid = mlid,
    uid = uid,
    name = name,
    title = title,
    discordmoji = discordmoji,
    releaseYear = releaseYear,
    heroClass = heroClass,
    roles = role,
    lanes = lane,
    difficulty = difficulty,
    description = description,
    iconUrl = iconUrl,
    portrait = portrait,
    skillsJson = json.encodeToString(skills.map { it.toDto() }),
    buildsJson = json.encodeToString(recommendedBuilds.map { it.toDto() }),
    emblemJson = json.encodeToString(recommendedEmblem.toDto()),
    spellsJson = json.encodeToString(recommendedSpells),
    countersJson = json.encodeToString(counters),
    strongAgainstJson = json.encodeToString(strongAgainst),
    tipsJson = json.encodeToString(tips),
    combosJson = json.encodeToString(combos.map { it.toDto() }),
)

// ─── Note Mapper ──────────────────────────────────────────────────────────────

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    content = content,
    title = title,
    color = color,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    content = content,
    title = title,
    color = color,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ─── DTO Shims (for JSON serialization inside Room) ───────────────────────────
// These lightweight DTOs avoid putting @Serializable on domain models

@kotlinx.serialization.Serializable
data class HeroSkillDto(
    val key: String,
    val name: String,
    val description: String,
    val cooldown: List<Float> = emptyList(),
    val manaCost: List<Int> = emptyList(),
    val cooldownString: String = "",
    val manaCostString: String = "",
    val type: String,
    val typeString: String = "",
    val iconUrl: String = ""
) {
    fun toDomain() = HeroSkill(
        key = runCatching { SkillKey.valueOf(key) }.getOrDefault(SkillKey.PASSIVE),
        name = name,
        description = description,
        cooldown = cooldown,
        manaCost = manaCost,
        cooldownString = cooldownString,
        manaCostString = manaCostString,
        type = runCatching { SkillType.valueOf(type) }.getOrDefault(SkillType.ACTIVE),
        typeString = typeString,
        iconUrl = iconUrl
    )
}

fun HeroSkill.toDto() = HeroSkillDto(
    key = key.name, name = name, description = description,
    cooldown = cooldown, manaCost = manaCost, 
    cooldownString = cooldownString, manaCostString = manaCostString,
    type = type.name, typeString = typeString, iconUrl = iconUrl
)

@kotlinx.serialization.Serializable
data class HeroBuildDto(
    val id: String,
    val name: String,
    val phase: String,
    val items: List<BuildItemDto>,
    val description: String = "",
    val playstyle: String = "STANDARD"
) {
    fun toDomain() = HeroBuild(
        id = id, name = name,
        phase = runCatching { BuildPhase.valueOf(phase) }.getOrDefault(BuildPhase.MID),
        items = items.map { it.toDomain() },
        description = description,
        playstyle = runCatching { BuildPlaystyle.valueOf(playstyle) }.getOrDefault(BuildPlaystyle.STANDARD)
    )
}

fun HeroBuild.toDto() = HeroBuildDto(
    id = id, name = name, phase = phase.name,
    items = items.map { it.toDto() }, description = description, playstyle = playstyle.name
)

@kotlinx.serialization.Serializable
data class BuildItemDto(val id: String, val name: String, val price: Int, val iconUrl: String = "", val description: String = "") {
    fun toDomain() = BuildItem(id = id, name = name, price = price, iconUrl = iconUrl, description = description)
}
fun BuildItem.toDto() = BuildItemDto(id = id, name = name, price = price, iconUrl = iconUrl, description = description)

@kotlinx.serialization.Serializable
data class HeroEmblemDto(val name: String, val tier: Int, val talents: List<String>, val description: String = "") {
    fun toDomain() = HeroEmblem(name = name, tier = tier, talents = talents, description = description)
}
fun HeroEmblem.toDto() = HeroEmblemDto(name = name, tier = tier, talents = talents, description = description)

@kotlinx.serialization.Serializable
data class HeroComboDto(
    val id: String, val name: String, val description: String,
    val steps: List<ComboStepDto>, val difficulty: String = "MODERATE", val notes: String = ""
) {
    fun toDomain() = HeroCombo(
        id = id, name = name, description = description,
        steps = steps.map { it.toDomain() },
        difficulty = runCatching { HeroDifficulty.valueOf(difficulty) }.getOrDefault(HeroDifficulty.MODERATE),
        notes = notes
    )
}
fun HeroCombo.toDto() = HeroComboDto(
    id = id, name = name, description = description,
    steps = steps.map { it.toDto() }, difficulty = difficulty.name, notes = notes
)

@kotlinx.serialization.Serializable
data class ComboStepDto(val order: Int, val skillKey: String, val description: String, val timing: String = "") {
    fun toDomain() = ComboStep(
        order = order,
        skillKey = runCatching { SkillKey.valueOf(skillKey) }.getOrDefault(SkillKey.SKILL_1),
        description = description, timing = timing
    )
}
fun ComboStep.toDto() = ComboStepDto(order = order, skillKey = skillKey.name, description = description, timing = timing)
