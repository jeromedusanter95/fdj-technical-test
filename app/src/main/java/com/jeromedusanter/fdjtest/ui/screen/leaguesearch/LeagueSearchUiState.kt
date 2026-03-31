package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

import com.jeromedusanter.fdjtest.domain.model.League

data class LeagueSearchUiState(
    val searchQuery: String = "",
    val leagues: List<League> = emptyList(),
    val filteredLeagues: List<League> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNoResults: Boolean = false
)
