package com.jeromedusanter.fdjtest.ui.screen.teamslist

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getTeamsByLeagueUseCase: GetTeamsByLeagueUseCase
    private lateinit var viewModel: TeamsListViewModel

    private val testLeagueName = "French Ligue 1"

    @Before
    fun setup() {
        getTeamsByLeagueUseCase = mockk()

        savedStateHandle = SavedStateHandle().apply {
            set("leagueName", testLeagueName)
        }
    }

    @Test
    fun `init should load teams successfully`() = runTest {
        // Given
        val teams = listOf(
            Team("1", "Paris Saint-Germain", "badge1.png", testLeagueName),
            Team("2", "Olympique Marseille", "badge2.png", testLeagueName)
        )
        coEvery { getTeamsByLeagueUseCase(testLeagueName) } returns Result.success(teams)

        // When
        viewModel = TeamsListViewModel(savedStateHandle, getTeamsByLeagueUseCase)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testLeagueName, state.leagueName)
            assertFalse(state.isLoading)
            assertEquals(teams, state.teams)
            assertNull(state.errorMessage)
        }
        coVerify(exactly = 1) { getTeamsByLeagueUseCase(testLeagueName) }
    }

    @Test
    fun `init should handle error when loading teams fails`() = runTest {
        // Given
        val exception = Exception("Failed to load teams")
        coEvery { getTeamsByLeagueUseCase(testLeagueName) } returns Result.failure(exception)

        // When
        viewModel = TeamsListViewModel(savedStateHandle, getTeamsByLeagueUseCase)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testLeagueName, state.leagueName)
            assertFalse(state.isLoading)
            assertEquals("Failed to load teams", state.errorMessage)
            assertEquals(emptyList<Team>(), state.teams)
        }
    }

    @Test
    fun `init should handle empty teams list`() = runTest {
        // Given
        coEvery { getTeamsByLeagueUseCase(testLeagueName) } returns Result.success(emptyList())

        // When
        viewModel = TeamsListViewModel(savedStateHandle, getTeamsByLeagueUseCase)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testLeagueName, state.leagueName)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            assertEquals(emptyList<Team>(), state.teams)
        }
    }

    @Test
    fun `retry should reload teams successfully after initial error`() = runTest {
        // Given
        coEvery { getTeamsByLeagueUseCase(testLeagueName) } returns Result.failure(Exception("Error"))
        viewModel = TeamsListViewModel(savedStateHandle, getTeamsByLeagueUseCase)
        advanceUntilIdle()

        val teams = listOf(
            Team("1", "Paris Saint-Germain", "badge1.png", testLeagueName)
        )
        coEvery { getTeamsByLeagueUseCase(testLeagueName) } returns Result.success(teams)

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(teams, state.teams)
            assertNull(state.errorMessage)
        }
        coVerify(exactly = 2) { getTeamsByLeagueUseCase(testLeagueName) }
    }

    @Test
    fun `loading state should be true while loading teams`() = runTest {
        // Given
        coEvery { getTeamsByLeagueUseCase(testLeagueName) } returns Result.success(emptyList())

        // When
        viewModel = TeamsListViewModel(savedStateHandle, getTeamsByLeagueUseCase)

        // Then
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            advanceUntilIdle()
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
        }
    }
}
