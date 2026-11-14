package com.playwithme.assistant.feature.hero

import com.playwithme.assistant.core.common.UiState
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.domain.model.HeroRole

data class HeroListUiState(
    val heroes: List<Hero> = emptyList(),
    val filteredHeroes: List<Hero> = emptyList(),
    val searchQuery: String = "",
    val selectedRole: HeroRole? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class HeroDetailUiState(
    val hero: Hero? = null,
    val counterHeroes: List<Hero> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
