package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.usecase.GetAllLeaguesUseCase
import com.jeromedusanter.fdjtest.domain.usecase.SearchLeaguesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueSearchViewModel @Inject constructor(
    private val getAllLeaguesUseCase: GetAllLeaguesUseCase,
    private val searchLeaguesUseCase: SearchLeaguesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueSearchUiState())
    val uiState: StateFlow<LeagueSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadLeagues()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(filteredLeagues = emptyList(), errorMessage = null, showNoResults = false) }
        } else {
            searchJob = viewModelScope.launch {
                delay(SEARCH_DELAY)
                searchLeagues(query)
            }
        }
    }

    fun onLeagueSelected(league: League) {
        _uiState.update { it.copy(searchQuery = league.name, filteredLeagues = emptyList()) }
    }

    private fun loadLeagues() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getAllLeaguesUseCase()
                .onSuccess { leagues ->
                    _uiState.update {
                        it.copy(
                            leagues = leagues,
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
        }
    }

    private fun searchLeagues(query: String) {
        viewModelScope.launch {
            searchLeaguesUseCase(query)
                .onSuccess { leagues ->
                    _uiState.update {
                        it.copy(
                            filteredLeagues = leagues,
                            showNoResults = query.length > 2 && leagues.isEmpty(),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            errorMessage = exception.message,
                            showNoResults = false
                        )
                    }
                }
        }
    }

    fun retry() {
        loadLeagues()
    }

    companion object {
        private const val SEARCH_DELAY: Long = 300L
    }
}
