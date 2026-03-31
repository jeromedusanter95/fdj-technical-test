package com.jeromedusanter.fdjtest.ui.screen.teamslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jeromedusanter.fdjtest.domain.model.Result
import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.usecase.GetTeamsByLeagueUseCase
import com.jeromedusanter.fdjtest.ui.navigation.TeamsListScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamsListUiState(
    val leagueName: String = "",
    val teams: List<Team> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TeamsListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase
) : ViewModel() {

    private val teamsListScreen: TeamsListScreen = savedStateHandle.toRoute()

    private val _uiState = MutableStateFlow(TeamsListUiState(leagueName = teamsListScreen.leagueName))
    val uiState: StateFlow<TeamsListUiState> = _uiState.asStateFlow()

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getTeamsByLeagueUseCase(teamsListScreen.leagueName)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            teams = result.data,
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

    fun retry() {
        loadTeams()
    }
}
