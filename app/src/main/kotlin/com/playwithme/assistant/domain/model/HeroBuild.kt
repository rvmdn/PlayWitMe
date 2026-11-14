package com.playwithme.assistant.domain.model

/**
 * Represents a hero's skill/ability.
 */
data class HeroSkill(
    val key: SkillKey,
    val name: String,
    val description: String,
    val cooldown: List<Float> = emptyList(), // Legacy or parsed CD array
    val manaCost: List<Int> = emptyList(),   // Legacy or parsed Mana array
    val cooldownString: String = "",         // Raw string from JSON e.g. "8.0" or "null"
    val manaCostString: String = "",         // Raw string from JSON e.g. "75 / 85 / 95"
    val type: SkillType,
    val typeString: String = "",             // Raw string from JSON e.g. "passive", "active"
    val iconUrl: String = ""
)

enum class SkillKey {
    PASSIVE,
    SKILL_1,
    SKILL_2,
    SKILL_3,
    ULTIMATE,
    BASIC_ATTACK,
    BATTLE_SPELL
}

enum class SkillType {
    PASSIVE,
    ACTIVE,
    TOGGLE
}

/**
 * Represents a hero build recommendation.
 */
data class HeroBuild(
    val id: String,
    val name: String,
    val phase: BuildPhase,
    val items: List<BuildItem>,
    val description: String = "",
    val playstyle: BuildPlaystyle = BuildPlaystyle.STANDARD
)

enum class BuildPhase {
    EARLY,   // First 3 items
    MID,     // Full core build
    LATE,    // Full 6-item build
    COUNTER  // Counter-build items
}

enum class BuildPlaystyle {
    STANDARD,
    AGGRESSIVE,
    DEFENSIVE,
    HYBRID
}

/**
 * Represents a single item in a build.
 */
data class BuildItem(
    val id: String,
    val name: String,
    val price: Int,
    val iconUrl: String = "",
    val description: String = ""
)

/**
 * Recommended emblem for a hero.
 */
data class HeroEmblem(
    val name: String,
    val tier: Int, // 1–3
    val talents: List<String>,
    val description: String = ""
)

/**
 * A step-by-step combo guide for a hero.
 * This is INFORMATIONAL ONLY — never automation.
 */
data class HeroCombo(
    val id: String,
    val name: String,
    val description: String,
    val steps: List<ComboStep>,
    val difficulty: com.playwithme.assistant.domain.model.HeroDifficulty = com.playwithme.assistant.domain.model.HeroDifficulty.MODERATE,
    val notes: String = "",
    val authorName: String = "Community",
    val likes: Int = 0
)

/**
 * A single step in a combo (skill to use).
 */
data class ComboStep(
    val order: Int,
    val skillKey: SkillKey,
    val description: String,
    val timing: String = "" // e.g. "After dash lands", "When enemy is CC'd"
)
