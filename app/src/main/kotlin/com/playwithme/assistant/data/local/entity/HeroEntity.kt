package com.playwithme.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playwithme.assistant.domain.model.HeroDifficulty
import com.playwithme.assistant.domain.model.HeroLane
import com.playwithme.assistant.domain.model.HeroRole

/**
 * Room entity for Hero.
 *
 * Stores lists as pipe-separated strings via [RoomConverters].
 * JSON-serialized fields (skills, builds, combos) stored as JSON strings
 * to avoid complex relational joins for this read-heavy, rarely-updated table.
 */
@Entity(tableName = "heroes")
data class HeroEntity(
    @PrimaryKey val id: String,
    val mlid: String = "",
    val uid: String = "",
    val name: String,
    val title: String,
    val discordmoji: String = "",
    val releaseYear: String = "",
    val heroClass: String = "",
    val roles: List<HeroRole>,        // Converted by RoomConverters
    val lanes: List<HeroLane>,        // Converted by RoomConverters
    val difficulty: HeroDifficulty,   // Converted by RoomConverters
    val description: String,
    val iconUrl: String,
    val portrait: String = "",
    val skillsJson: String,           // JSON serialized List<HeroSkill>
    val buildsJson: String,           // JSON serialized List<HeroBuild>
    val emblemJson: String,           // JSON serialized HeroEmblem
    val spellsJson: String,           // JSON: List<String>
    val countersJson: String,         // JSON: List<String> hero IDs
    val strongAgainstJson: String,    // JSON: List<String> hero IDs
    val tipsJson: String,             // JSON: List<String>
    val combosJson: String,           // JSON serialized List<HeroCombo>
    val updatedAt: Long = System.currentTimeMillis()
)
