package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.ui.components.ErrorMessage
import com.jeromedusanter.fdjtest.ui.components.LeagueSearchItem
import com.jeromedusanter.fdjtest.ui.components.LoadingIndicator
import com.jeromedusanter.fdjtest.ui.components.SearchTextField

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeagueSearchContent(
    uiState: LeagueSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onLeagueClick: (League) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Search League") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SearchTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Search league..."
            )

            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                uiState.errorMessage != null -> {
                    ErrorMessage(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                }
                uiState.filteredLeagues.isNotEmpty() -> {
                    LazyColumn {
                        items(
                            items = uiState.filteredLeagues,
                            key = { it.id }
                        ) { league ->
                            LeagueSearchItem(
                                leagueName = league.name,
                                sport = league.sport,
                                onClick = { onLeagueClick(league) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchScreenLoadingPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(isLoading = true),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchScreenWithResultsPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(
            searchQuery = "French",
            filteredLeagues = listOf(
                League("1", "French Ligue 1", "Soccer"),
                League("2", "French Ligue 2", "Soccer")
            )
        ),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchScreenErrorPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(errorMessage = "Failed to load leagues"),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}
