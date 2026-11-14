package com.playwithme.assistant.feature.draft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.domain.repository.HeroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class DraftState(
    val alliedPicks: List<Hero?> = List(5) { null },
    val enemyPicks: List<Hero?> = List(5) { null },
    val alliedBans: List<Hero?> = List(5) { null },
    val enemyBans: List<Hero?> = List(5) { null },
    val suggestedCounters: List<Hero> = emptyList(),
    val isAutoDetectEnabled: Boolean = true
)

@HiltViewModel
class DraftViewModel @Inject constructor(
    private val heroRepository: HeroRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DraftState())
    val state: StateFlow<DraftState> = _state.asStateFlow()

    fun toggleAutoDetect() {
        _state.update { it.copy(isAutoDetectEnabled = !it.isAutoDetectEnabled) }
    }

    fun setAlliedPick(index: Int, hero: Hero?) {
        _state.update {
            val newList = it.alliedPicks.toMutableList()
            newList[index] = hero
            it.copy(alliedPicks = newList)
        }
    }

    fun setEnemyPick(index: Int, hero: Hero?) {
        _state.update {
            val newList = it.enemyPicks.toMutableList()
            newList[index] = hero
            it.copy(enemyPicks = newList)
        }
        calculateCounters()
    }

    fun setAlliedBan(index: Int, hero: Hero?) {
        _state.update {
            val newList = it.alliedBans.toMutableList()
            newList[index] = hero
            it.copy(alliedBans = newList)
        }
    }

    fun setEnemyBan(index: Int, hero: Hero?) {
        _state.update {
            val newList = it.enemyBans.toMutableList()
            newList[index] = hero
            it.copy(enemyBans = newList)
        }
    }
    
    fun resetDraft() {
        _state.value = DraftState(isAutoDetectEnabled = _state.value.isAutoDetectEnabled)
    }

    private fun calculateCounters() {
        val currentState = _state.value
        val enemyHeroes = currentState.enemyPicks.filterNotNull()
        
        if (enemyHeroes.isEmpty()) {
            _state.update { it.copy(suggestedCounters = emptyList()) }
            return
        }

        viewModelScope.launch {
            // Very simple recommendation logic: get counter heroes for all enemy picks,
            // tally them up, and suggest the top 3 most recurring counters that are not already picked or banned.
            val counterMap = mutableMapOf<String, Hero>()
            val scoreMap = mutableMapOf<String, Int>()

            for (enemy in enemyHeroes) {
                val result = heroRepository.getCounterHeroes(enemy.id)
                if (result is Result.Success) {
                    for (counter in result.data) {
                        counterMap[counter.id] = counter
                        scoreMap[counter.id] = scoreMap.getOrDefault(counter.id, 0) + 1
                    }
                }
            }

            // Exclude already picked/banned heroes
            val excludeIds = (currentState.alliedPicks + currentState.enemyPicks + 
                            currentState.alliedBans + currentState.enemyBans)
                            .filterNotNull()
                            .map { it.id }
                            .toSet()

            val topCounters = scoreMap.entries
                .filter { it.key !in excludeIds }
                .sortedByDescending { it.value }
                .take(3)
                .mapNotNull { counterMap[it.key] }

            _state.update { it.copy(suggestedCounters = topCounters) }
        }
    }
}
