package com.jeromedusanter.fdjtest.ui.screen.leaguesearch.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.ui.components.EmptyState
import com.jeromedusanter.fdjtest.ui.components.ErrorState
import com.jeromedusanter.fdjtest.ui.components.LoadingIndicator
import com.jeromedusanter.fdjtest.ui.components.SearchTextField
import com.jeromedusanter.fdjtest.ui.screen.leaguesearch.LeagueSearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueSearchContent(
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
                    ErrorState(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )
                }
                uiState.showNoResults -> {
                    EmptyState(
                        message = "No leagues found for \"${uiState.searchQuery}\""
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
private fun LeagueSearchContentLoadingPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(
            searchQuery = "Liga",
            isLoading = true
        ),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchContentWithResultsPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(
            searchQuery = "Liga",
            filteredLeagues = listOf(
                League("1", "French Ligue 1", "Soccer"),
                League("2", "Spanish La Liga", "Soccer"),
                League("3", "Portuguese Liga", "Soccer")
            )
        ),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchContentNoResultsPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(
            searchQuery = "xyz",
            showNoResults = true
        ),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LeagueSearchContentErrorPreview() {
    LeagueSearchContent(
        uiState = LeagueSearchUiState(
            searchQuery = "Liga",
            errorMessage = "Failed to load leagues"
        ),
        onSearchQueryChange = {},
        onLeagueClick = {},
        onRetry = {}
    )
}
