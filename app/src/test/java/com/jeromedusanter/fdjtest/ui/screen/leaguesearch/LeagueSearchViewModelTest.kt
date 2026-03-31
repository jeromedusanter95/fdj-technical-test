package com.jeromedusanter.fdjtest.ui.screen.leaguesearch

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

    private val testLeagues = listOf(
        League(id = "1", name = "English Premier League", sport = "Soccer"),
        League(id = "2", name = "Spanish La Liga", sport = "Soccer"),
        League(id = "3", name = "German Bundesliga", sport = "Soccer")
    )

    @Before
    fun setup() {
        getAllLeaguesUseCase = mockk()
        searchLeaguesUseCase = mockk()
    }

    private fun createViewModel(): LeagueSearchViewModel {
        return LeagueSearchViewModel(
            getAllLeaguesUseCase = getAllLeaguesUseCase,
            searchLeaguesUseCase = searchLeaguesUseCase
        )
    }

    @Test
    fun `init should load leagues successfully`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(testLeagues, state.leagues)
        assertTrue(state.filteredLeagues.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertFalse(state.showNoResults)
        coVerify(exactly = 1) { getAllLeaguesUseCase() }
    }

    @Test
    fun `init should handle error when loading leagues fails`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network error"
        coEvery { getAllLeaguesUseCase() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.leagues.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.errorMessage)
    }

    @Test
    fun `onSearchQueryChange should update search query immediately`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase(any()) } returns Result.success(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("English")

        // Then
        val state = viewModel.uiState.value
        assertEquals("English", state.searchQuery)
    }

    @Test
    fun `onSearchQueryChange should trigger search after delay`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        val searchResults = listOf(testLeagues[0])
        coEvery { searchLeaguesUseCase("English") } returns Result.success(searchResults)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("English")
        advanceTimeBy(300L) // Wait for debounce delay
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("English", state.searchQuery)
        assertEquals(searchResults, state.filteredLeagues)
        assertFalse(state.showNoResults)
        coVerify(exactly = 1) { searchLeaguesUseCase("English") }
    }

    @Test
    fun `onSearchQueryChange should cancel previous search when query changes`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase("Eng") } returns Result.success(listOf(testLeagues[0]))
        coEvery { searchLeaguesUseCase("English") } returns Result.success(listOf(testLeagues[0]))
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("Eng")
        advanceTimeBy(100L) // Less than debounce delay
        viewModel.onSearchQueryChange("English")
        advanceTimeBy(300L) // Complete the second search
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { searchLeaguesUseCase("Eng") } // First search was cancelled
        coVerify(exactly = 1) { searchLeaguesUseCase("English") }
    }

    @Test
    fun `onSearchQueryChange with blank query should clear filtered leagues`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase("English") } returns Result.success(listOf(testLeagues[0]))
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onSearchQueryChange("English")
        advanceTimeBy(300L)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.filteredLeagues.isEmpty())
        assertFalse(state.showNoResults)
        assertNull(state.errorMessage)
    }

    @Test
    fun `onSearchQueryChange should show no results when search returns empty list with query length greater than 2`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase("xyz") } returns Result.success(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("xyz")
        advanceTimeBy(300L)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("xyz", state.searchQuery)
        assertTrue(state.filteredLeagues.isEmpty())
        assertTrue(state.showNoResults)
    }

    @Test
    fun `onSearchQueryChange should not show no results when query length is 2 or less`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase("xy") } returns Result.success(emptyList())
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("xy")
        advanceTimeBy(300L)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("xy", state.searchQuery)
        assertFalse(state.showNoResults)
    }

    @Test
    fun `onSearchQueryChange should handle search error`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Search failed"
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase("English") } returns Result.failure(Exception(errorMessage))
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("English")
        advanceTimeBy(300L)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(errorMessage, state.errorMessage)
        assertFalse(state.showNoResults)
    }

    @Test
    fun `onLeagueSelected should update search query and clear filtered leagues`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.success(testLeagues)
        coEvery { searchLeaguesUseCase("English") } returns Result.success(listOf(testLeagues[0]))
        viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onSearchQueryChange("English")
        advanceTimeBy(300L)
        advanceUntilIdle()

        // When
        viewModel.onLeagueSelected(testLeagues[0])
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("English Premier League", state.searchQuery)
        assertTrue(state.filteredLeagues.isEmpty())
    }

    @Test
    fun `retry should reload leagues successfully`() = runTest(testDispatcher) {
        // Given
        coEvery { getAllLeaguesUseCase() } returns Result.failure(Exception("First error")) andThen Result.success(testLeagues)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(testLeagues, state.leagues)
        assertNull(state.errorMessage)
        coVerify(exactly = 2) { getAllLeaguesUseCase() }
    }
}
