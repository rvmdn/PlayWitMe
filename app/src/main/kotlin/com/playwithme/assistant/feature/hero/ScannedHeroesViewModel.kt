package com.playwithme.assistant.feature.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.domain.model.Hero
import com.playwithme.assistant.domain.repository.HeroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ScannedHeroesUiState(
    val heroes: List<Hero> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ScannedHeroesViewModel @Inject constructor(
    private val heroRepository: HeroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannedHeroesUiState())
    val uiState: StateFlow<ScannedHeroesUiState> = _uiState.asStateFlow()

    init {
        observeScannedHeroes()
    }

    private fun observeScannedHeroes() {
        heroRepository.getScannedHeroes()
            .onEach { scannedHeroes ->
                _uiState.update { it.copy(
                    heroes = scannedHeroes,
                    isLoading = false
                ) }
            }
            .launchIn(viewModelScope)
    }
}
