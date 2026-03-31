package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Result
import com.jeromedusanter.fdjtest.domain.usecase.GetAllLeaguesUseCase
import com.jeromedusanter.fdjtest.domain.usecase.SearchLeaguesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeagueSearchUiState(
    val searchQuery: String = "",
    val leagues: List<League> = emptyList(),
    val filteredLeagues: List<League> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LeagueSearchViewModel @Inject constructor(
    private val getAllLeaguesUseCase: GetAllLeaguesUseCase,
    private val searchLeaguesUseCase: SearchLeaguesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeagueSearchUiState())
    val uiState: StateFlow<LeagueSearchUiState> = _uiState.asStateFlow()

    init {
        loadLeagues()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchLeagues(query)
    }

    fun onLeagueSelected(league: League) {
        _uiState.update { it.copy(searchQuery = league.name, filteredLeagues = emptyList()) }
    }

    private fun loadLeagues() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getAllLeaguesUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            leagues = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    private fun searchLeagues(query: String) {
        viewModelScope.launch {
            when (val result = searchLeaguesUseCase(query)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(filteredLeagues = result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(errorMessage = result.exception.message)
                    }
                }
            }
        }
    }

    fun retry() {
        loadLeagues()
    }
}
