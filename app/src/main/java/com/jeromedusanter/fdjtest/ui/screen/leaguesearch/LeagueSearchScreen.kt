package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeromedusanter.fdjtest.ui.screen.leaguesearch.components.LeagueSearchContent

@Composable
fun LeagueSearchScreen(
    onLeagueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeagueSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeagueSearchContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onLeagueClick = { league ->
            viewModel.onLeagueSelected(league)
            onLeagueSelected(league.name)
        },
        onRetry = viewModel::retry,
        modifier = modifier
    )
}
