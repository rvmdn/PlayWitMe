package com.playwithme.assistant.feature.scanner

import timber.log.Timber

/**
 * Utility for dumping scanned hero data into JSON format
 * for easy copy-pasting by the developer into hero_data.json
 */
object HeroJsonDumper {
    fun dumpJson(heroId: String, heroName: String, iconPath: String) {
        val json = """
            {
              "id": "$heroId",
              "name": "$heroName",
              "title": "",
              "roles": [],
              "lanes": [],
              "difficulty": "EASY",
              "description": "",
              "icon_url": "$iconPath",
              "skills": [],
              "build_recommendations": [],
              "emblem": {"name": "", "tier": 0, "talents": []},
              "spells": [],
              "counters": [],
              "strong_against": [],
              "tips": [],
              "combos": []
            },
        """.trimIndent()
        
        Timber.d("====== NEW HERO SCANNED: $heroName ======")
        Timber.d(json)
        Timber.d("======================================")
    }
}
