package com.jeromedusanter.fdjtest.ui.screen.teamslist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeromedusanter.fdjtest.R
import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.ui.components.EmptyState
import com.jeromedusanter.fdjtest.ui.components.ErrorState
import com.jeromedusanter.fdjtest.ui.components.LoadingIndicator
import com.jeromedusanter.fdjtest.ui.screen.teamslist.TeamsListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsListContent(
    uiState: TeamsListUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.leagueName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            uiState.errorMessage != null -> {
                ErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.teams.isEmpty() -> {
                EmptyState(
                    message = stringResource(R.string.no_teams_found),
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.teams,
                        key = { it.id }
                    ) { team ->
                        TeamCard(
                            teamName = team.name,
                            badgeUrl = team.badgeUrl
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamsListContentLoadingPreview() {
    TeamsListContent(
        uiState = TeamsListUiState(
            leagueName = "French Ligue 1",
            isLoading = true
        ),
        onBackClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TeamsListContentWithTeamsPreview() {
    TeamsListContent(
        uiState = TeamsListUiState(
            leagueName = "French Ligue 1",
            teams = listOf(
                Team("1", "Paris Saint-Germain", "https://example.com/psg.png", "French Ligue 1"),
                Team("2", "Olympique Marseille", "https://example.com/om.png", "French Ligue 1"),
                Team("3", "AS Monaco", "https://example.com/monaco.png", "French Ligue 1"),
                Team("4", "Lille OSC", "https://example.com/lille.png", "French Ligue 1")
            )
        ),
        onBackClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TeamsListContentErrorPreview() {
    TeamsListContent(
        uiState = TeamsListUiState(
            leagueName = "French Ligue 1",
            errorMessage = "Failed to load teams"
        ),
        onBackClick = {},
        onRetry = {}
    )
}
