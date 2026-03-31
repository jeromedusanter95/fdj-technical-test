package com.jeromedusanter.fdjtest.ui.screen.teamslist

import com.jeromedusanter.fdjtest.domain.model.Team

data class TeamsListUiState(
    val leagueName: String = "",
    val teams: List<Team> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
