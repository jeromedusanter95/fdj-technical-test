package com.jeromedusanter.fdjtest.ui.screen.teamslist

import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.usecase.GetTeamsByLeagueUseCase
import com.jeromedusanter.fdjtest.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase
    private lateinit var viewModel: TeamsListViewModel

    private fun createViewModel(leagueName: String): TeamsListViewModel {
        return TeamsListViewModel(
            getTeamsByLeagueUseCase = getTeamsByLeagueUseCase,
            leagueName = leagueName
        )
    }

    @Test
    fun `init should set league name and load teams successfully`() = runTest(testDispatcher) {
        // Given
        val leagueName = "French Ligue 1"
        val teams = listOf(
            Team(id = "1", name = "Paris Saint-Germain", badgeUrl = "badge1.png", league = leagueName),
            Team(id = "2", name = "Olympique Marseille", badgeUrl = "badge2.png", league = leagueName)
        )
        getTeamsByLeagueUseCase = mockk()
        coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.success(teams)

        // When
        viewModel = createViewModel(leagueName)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(leagueName, state.leagueName)
        assertEquals(teams, state.teams)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        coVerify(exactly = 1) { getTeamsByLeagueUseCase(leagueName) }
    }

    @Test
    fun `init should set loading state then success state`() = runTest(testDispatcher) {
        // Given
        val leagueName = "Spanish La Liga"
        val teams = listOf(
            Team(id = "1", name = "Real Madrid", badgeUrl = "badge.png", league = leagueName)
        )
        getTeamsByLeagueUseCase = mockk()
        coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.success(teams)

        // When
        viewModel = createViewModel(leagueName)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertEquals(leagueName, finalState.leagueName)
        assertEquals(teams, finalState.teams)
        assertFalse(finalState.isLoading)
        assertNull(finalState.errorMessage)
    }

    @Test
    fun `init should handle error when loading teams fails`() = runTest(testDispatcher) {
        // Given
        val leagueName = "German Bundesliga"
        val errorMessage = "Network error"
        getTeamsByLeagueUseCase = mockk()
        coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = createViewModel(leagueName)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(leagueName, state.leagueName)
        assertTrue(state.teams.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.errorMessage)
        coVerify(exactly = 1) { getTeamsByLeagueUseCase(leagueName) }
    }

    @Test
    fun `init should handle empty teams list`() = runTest(testDispatcher) {
        // Given
        val leagueName = "Italian Serie A"
        getTeamsByLeagueUseCase = mockk()
        coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.success(emptyList())

        // When
        viewModel = createViewModel(leagueName)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(leagueName, state.leagueName)
        assertTrue(state.teams.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `retry should reload teams successfully`() = runTest(testDispatcher) {
        // Given
        val leagueName = "English Premier League"
        val teams = listOf(
            Team(id = "1", name = "Manchester United", badgeUrl = "badge.png", league = leagueName)
        )
        getTeamsByLeagueUseCase = mockk()
        coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.failure(Exception("First error")) andThen Result.success(teams)

        viewModel = createViewModel(leagueName)
        advanceUntilIdle()

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(teams, state.teams)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        coVerify(exactly = 2) { getTeamsByLeagueUseCase(leagueName) }
    }

    @Test
    fun `retry should clear error message and reload teams`() = runTest(testDispatcher) {
        // Given
        val leagueName = "Portuguese Liga"
        val teams = listOf(
            Team(id = "1", name = "FC Porto", badgeUrl = "badge.png", league = leagueName)
        )
        getTeamsByLeagueUseCase = mockk()
        coEvery { getTeamsByLeagueUseCase(leagueName) } returns Result.failure(Exception("Error")) andThen Result.success(teams)

        viewModel = createViewModel(leagueName)
        advanceUntilIdle()

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertEquals(teams, state.teams)
    }
}
