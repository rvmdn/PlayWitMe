package com.playwithme.assistant.data.local

import androidx.room.TypeConverter
import com.playwithme.assistant.domain.model.HeroDifficulty
import com.playwithme.assistant.domain.model.HeroLane
import com.playwithme.assistant.domain.model.HeroRole
import com.playwithme.assistant.domain.model.NoteColor

/**
 * Type converters for Room to handle complex types like enums and lists.
 */
class RoomConverters {

    // ─── List<String> ────────────────────────────────────────────────────
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("|")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")

    // ─── HeroRole List ────────────────────────────────────────────────────
    @TypeConverter
    fun fromHeroRoleList(value: List<HeroRole>): String =
        value.joinToString(",") { it.name }

    @TypeConverter
    fun toHeroRoleList(value: String): List<HeroRole> =
        if (value.isBlank()) emptyList()
        else value.split(",").mapNotNull { runCatching { HeroRole.valueOf(it) }.getOrNull() }

    // ─── HeroLane List ────────────────────────────────────────────────────
    @TypeConverter
    fun fromHeroLaneList(value: List<HeroLane>): String =
        value.joinToString(",") { it.name }

    @TypeConverter
    fun toHeroLaneList(value: String): List<HeroLane> =
        if (value.isBlank()) emptyList()
        else value.split(",").mapNotNull { runCatching { HeroLane.valueOf(it) }.getOrNull() }

    // ─── HeroDifficulty ───────────────────────────────────────────────────
    @TypeConverter
    fun fromHeroDifficulty(value: HeroDifficulty): String = value.name

    @TypeConverter
    fun toHeroDifficulty(value: String): HeroDifficulty =
        runCatching { HeroDifficulty.valueOf(value) }.getOrDefault(HeroDifficulty.MODERATE)

    // ─── NoteColor ────────────────────────────────────────────────────────
    @TypeConverter
    fun fromNoteColor(value: NoteColor): String = value.name

    @TypeConverter
    fun toNoteColor(value: String): NoteColor =
        runCatching { NoteColor.valueOf(value) }.getOrDefault(NoteColor.DEFAULT)
}
