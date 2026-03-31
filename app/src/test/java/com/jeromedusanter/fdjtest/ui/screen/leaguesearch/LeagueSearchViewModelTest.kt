package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

import app.cash.turbine.test
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.usecase.GetAllLeaguesUseCase
import com.jeromedusanter.fdjtest.domain.usecase.SearchLeaguesUseCase
import com.jeromedusanter.fdjtest.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeagueSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var getAllLeaguesUseCase: GetAllLeaguesUseCase
    private lateinit var searchLeaguesUseCase: SearchLeaguesUseCase
    private lateinit var viewModel: LeagueSearchViewModel

    @Before
    fun setup() {
        getAllLeaguesUseCase = mockk()
        searchLeaguesUseCase = mockk()
    }

    @Test
    fun `init should load all leagues successfully`() = runTest {
        // Given
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer")
        )
        coEvery { getAllLeaguesUseCase() } returns Result.success(leagues)

        // When
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(leagues, state.leagues)
            assertNull(state.errorMessage)
        }
        coVerify(exactly = 1) { getAllLeaguesUseCase() }
    }

    @Test
    fun `init should handle error when loading leagues fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { getAllLeaguesUseCase() } returns Result.failure(exception)

        // When
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.errorMessage)
            assertTrue(state.leagues.isEmpty())
        }
    }

    @Test
    fun `onSearchQueryChange should update search query immediately`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(emptyList())
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("test")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("test", state.searchQuery)
        }
    }

    @Test
    fun `onSearchQueryChange should debounce search and filter leagues`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(emptyList())
        val filteredLeagues = listOf(
            League("1", "English Premier League", "Soccer")
        )
        coEvery { searchLeaguesUseCase("league") } returns Result.success(filteredLeagues)
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("league")
        advanceTimeBy(300)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(filteredLeagues, state.filteredLeagues)
            assertNull(state.errorMessage)
            assertFalse(state.showNoResults)
        }
        coVerify(exactly = 1) { searchLeaguesUseCase("league") }
    }

    @Test
    fun `onSearchQueryChange should show no results when query returns empty list`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(emptyList())
        coEvery { searchLeaguesUseCase("xyz") } returns Result.success(emptyList())
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("xyz")
        advanceTimeBy(300)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.filteredLeagues.isEmpty())
            assertTrue(state.showNoResults)
            assertNull(state.errorMessage)
        }
    }

    @Test
    fun `onSearchQueryChange should clear results when query is blank`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(emptyList())
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.filteredLeagues.isEmpty())
            assertFalse(state.showNoResults)
            assertNull(state.errorMessage)
        }
        coVerify(exactly = 0) { searchLeaguesUseCase(any()) }
    }

    @Test
    fun `onSearchQueryChange should handle search error`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(emptyList())
        val exception = Exception("Search failed")
        coEvery { searchLeaguesUseCase("test") } returns Result.failure(exception)
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("test")
        advanceTimeBy(300)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Search failed", state.errorMessage)
            assertFalse(state.showNoResults)
        }
    }

    @Test
    fun `onLeagueSelected should update search query and clear filtered leagues`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(emptyList())
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()
        val selectedLeague = League("1", "English Premier League", "Soccer")

        // When
        viewModel.onLeagueSelected(selectedLeague)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("English Premier League", state.searchQuery)
            assertTrue(state.filteredLeagues.isEmpty())
        }
    }

    @Test
    fun `retry should reload leagues successfully`() = runTest {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.failure(Exception("Error"))
        viewModel = LeagueSearchViewModel(getAllLeaguesUseCase, searchLeaguesUseCase)
        advanceUntilIdle()

        val leagues = listOf(League("1", "English Premier League", "Soccer"))
        coEvery { getAllLeaguesUseCase() } returns Result.success(leagues)

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(leagues, state.leagues)
            assertNull(state.errorMessage)
        }
        coVerify(exactly = 2) { getAllLeaguesUseCase() }
    }
}
