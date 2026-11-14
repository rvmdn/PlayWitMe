package com.playwithme.assistant.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Remote API DTO for Hero data.
 * Only used in the data layer — never exposed to domain.
 */
@JsonClass(generateAdapter = true)
data class HeroRemoteDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "title") val title: String,
    @Json(name = "roles") val roles: List<String> = emptyList(),
    @Json(name = "lanes") val lanes: List<String> = emptyList(),
    @Json(name = "difficulty") val difficulty: String = "MODERATE",
    @Json(name = "description") val description: String = "",
    @Json(name = "icon_url") val iconUrl: String = "",
    @Json(name = "skills") val skills: List<SkillRemoteDto> = emptyList(),
    @Json(name = "builds") val builds: List<BuildRemoteDto> = emptyList(),
    @Json(name = "emblem") val emblem: EmblemRemoteDto? = null,
    @Json(name = "spells") val spells: List<String> = emptyList(),
    @Json(name = "counters") val counters: List<String> = emptyList(),
    @Json(name = "strong_against") val strongAgainst: List<String> = emptyList(),
    @Json(name = "tips") val tips: List<String> = emptyList(),
    @Json(name = "combos") val combos: List<ComboRemoteDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class SkillRemoteDto(
    @Json(name = "key") val key: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "cooldown") val cooldown: List<Float> = emptyList(),
    @Json(name = "mana_cost") val manaCost: List<Int> = emptyList(),
    @Json(name = "type") val type: String = "ACTIVE",
    @Json(name = "icon_url") val iconUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class BuildRemoteDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "phase") val phase: String,
    @Json(name = "items") val items: List<BuildItemRemoteDto>,
    @Json(name = "description") val description: String = "",
    @Json(name = "playstyle") val playstyle: String = "STANDARD"
)

@JsonClass(generateAdapter = true)
data class BuildItemRemoteDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Int = 0,
    @Json(name = "icon_url") val iconUrl: String = "",
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class EmblemRemoteDto(
    @Json(name = "name") val name: String,
    @Json(name = "tier") val tier: Int = 1,
    @Json(name = "talents") val talents: List<String> = emptyList(),
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class ComboRemoteDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "steps") val steps: List<ComboStepRemoteDto>,
    @Json(name = "difficulty") val difficulty: String = "MODERATE",
    @Json(name = "notes") val notes: String = ""
)

@JsonClass(generateAdapter = true)
data class ComboStepRemoteDto(
    @Json(name = "order") val order: Int,
    @Json(name = "skill_key") val skillKey: String,
    @Json(name = "description") val description: String,
    @Json(name = "timing") val timing: String = ""
)
