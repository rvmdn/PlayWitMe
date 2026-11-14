package com.playwithme.assistant.domain.repository

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.HeroCombo
import kotlinx.coroutines.flow.Flow

interface ComboRepository {
    fun getTopCombosForHero(heroId: String): Flow<List<HeroCombo>>
    suspend fun likeCombo(comboId: String): Result<Unit>
    suspend fun fetchCombosFromRemote(heroId: String): Result<Unit>
}
