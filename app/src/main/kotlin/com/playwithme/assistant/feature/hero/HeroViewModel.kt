package com.playwithme.assistant.feature.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.HeroRole
import com.playwithme.assistant.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroListViewModel @Inject constructor(
    private val getAllHeroesUseCase: GetAllHeroesUseCase,
    private val searchHeroUseCase: SearchHeroUseCase,
    private val getHeroesByRoleUseCase: GetHeroesByRoleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeroListUiState())
    val uiState: StateFlow<HeroListUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val selectedRole = MutableStateFlow<HeroRole?>(null)

    init {
        observeHeroes()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeHeroes() {
        combine(
            searchQuery.debounce(300),
            selectedRole
        ) { query, role -> Pair(query, role) }
            .flatMapLatest { (query, role) ->
                when {
                    query.isNotBlank() -> searchHeroUseCase(query)
                    role != null -> getHeroesByRoleUseCase(role)
                    else -> getAllHeroesUseCase()
                }
            }
            .onEach { heroes ->
                _uiState.update { state ->
                    state.copy(
                        filteredHeroes = heroes,
                        heroes = if (searchQuery.value.isBlank() && selectedRole.value == null) heroes else state.heroes,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onRoleSelected(role: HeroRole?) {
        selectedRole.value = role
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun clearSearch() {
        searchQuery.value = ""
        _uiState.update { it.copy(searchQuery = "") }
    }
}

@HiltViewModel
class HeroDetailViewModel @Inject constructor(
    private val getHeroByIdUseCase: GetHeroByIdUseCase,
    private val getCounterHeroUseCase: GetCounterHeroUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeroDetailUiState())
    val uiState: StateFlow<HeroDetailUiState> = _uiState.asStateFlow()

    fun loadHero(heroId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getHeroByIdUseCase(heroId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(hero = result.data, isLoading = false) }
                    loadCounters(heroId)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.message, isLoading = false)
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun loadCounters(heroId: String) {
        when (val result = getCounterHeroUseCase(heroId)) {
            is Result.Success -> _uiState.update { it.copy(counterHeroes = result.data) }
            else -> {}
        }
    }
}
