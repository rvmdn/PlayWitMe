package com.playwithme.assistant.domain.model

/**
 * Domain model representing a Mobile Legends hero.
 *
 * This is a pure Kotlin data class — no Android framework dependencies.
 * All fields are immutable for thread-safety and Compose compatibility.
 */
data class Hero(
    val id: String,
    val mlid: String = "",
    val uid: String = "",
    val name: String,
    val title: String,
    val discordmoji: String = "",
    val releaseYear: String = "",
    val heroClass: String = "", // Replacing single enum Role with string to support "Fighter, Assassin"
    val role: List<HeroRole> = emptyList(), // Keep for legacy/UI compatibility if needed
    val lane: List<HeroLane>,
    val difficulty: HeroDifficulty,
    val description: String,
    val iconUrl: String,
    val portrait: String = "",
    val skills: List<HeroSkill>,
    val recommendedBuilds: List<HeroBuild>,
    val recommendedEmblem: HeroEmblem,
    val recommendedSpells: List<String>,
    val counters: List<String>, // Hero IDs that counter this hero
    val strongAgainst: List<String>, // Hero IDs that this hero counters
    val tips: List<String>,
    val combos: List<HeroCombo>,
)

/** Hero role classification */
enum class HeroRole {
    FIGHTER,
    MAGE,
    MARKSMAN,
    ASSASSIN,
    SUPPORT,
    TANK
}

/** Preferred lane position */
enum class HeroLane {
    EXP,     // Experience lane (top)
    GOLD,    // Gold lane (bottom)
    MID,     // Mid lane
    JUNGLE,  // Jungle
    ROAM     // Roamer/Support
}

/** Hero mechanical difficulty */
enum class HeroDifficulty {
    EASY,
    MODERATE,
    HARD,
    VERY_HARD
}
