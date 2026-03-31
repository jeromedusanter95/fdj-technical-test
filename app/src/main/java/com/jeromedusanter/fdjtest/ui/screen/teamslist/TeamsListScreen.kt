package com.jeromedusanter.fdjtest.ui.screen.teamslist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeromedusanter.fdjtest.ui.screen.teamslist.components.TeamsListContent

@Composable
fun TeamsListScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeamsListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TeamsListContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}
