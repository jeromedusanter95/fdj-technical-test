package com.jeromedusanter.fdjtest.ui.screen.teamslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeromedusanter.fdjtest.domain.usecase.GetTeamsByLeagueUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TeamsListViewModel.Factory::class)
class TeamsListViewModel @AssistedInject constructor(
    private val getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase,
    @Assisted val leagueName: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamsListUiState(leagueName = leagueName))
    val uiState: StateFlow<TeamsListUiState> = _uiState.asStateFlow()

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getTeamsByLeagueUseCase(leagueName)
                .onSuccess { teams ->
                    _uiState.update {
                        it.copy(
                            teams = teams,
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

    fun retry() {
        loadTeams()
    }

    @AssistedFactory
    interface Factory {
        fun create(leagueName: String): TeamsListViewModel
    }
}
