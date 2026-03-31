package com.jeromedusanter.fdjtest.data.repository

import com.jeromedusanter.fdjtest.data.api.SportsApiService
import com.jeromedusanter.fdjtest.data.model.LeagueDto
import com.jeromedusanter.fdjtest.data.model.LeaguesResponse
import com.jeromedusanter.fdjtest.data.model.TeamDto
import com.jeromedusanter.fdjtest.data.model.TeamsResponse
import com.jeromedusanter.fdjtest.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SportsRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var apiService: SportsApiService
    private lateinit var repository: SportsRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = SportsRepositoryImpl(apiService, testDispatcher)
    }

    @Test
    fun `getAllLeagues should return success with leagues when API call succeeds`() = runTest {
        // Given
        val leagueDtos = listOf(
            LeagueDto(id = "1", name = "English Premier League", sport = "Soccer"),
            LeagueDto(id = "2", name = "Spanish La Liga", sport = "Soccer")
        )
        val response = LeaguesResponse(leagues = leagueDtos)
        coEvery { apiService.getAllLeagues() } returns response

        // When
        val result = repository.getAllLeagues()

        // Then
        assertTrue(result.isSuccess)
        val leagues = result.getOrNull()
        assertEquals(2, leagues?.size)
        assertEquals("English Premier League", leagues?.get(0)?.name)
        assertEquals("Soccer", leagues?.get(0)?.sport)
        coVerify(exactly = 1) { apiService.getAllLeagues() }
    }

    @Test
    fun `getAllLeagues should convert null fields to empty strings`() = runTest {
        // Given
        val leagueDtos = listOf(
            LeagueDto(id = "1", name = "English Premier League", sport = "Soccer"),
            LeagueDto(id = null, name = "League with null id", sport = "Soccer"),
            LeagueDto(id = "3", name = null, sport = "Soccer"),
            LeagueDto(id = "4", name = "Spanish La Liga", sport = null)
        )
        val response = LeaguesResponse(leagues = leagueDtos)
        coEvery { apiService.getAllLeagues() } returns response

        // When
        val result = repository.getAllLeagues()

        // Then
        assertTrue(result.isSuccess)
        val leagues = result.getOrNull()
        assertEquals(4, leagues?.size)
        assertEquals("English Premier League", leagues?.get(0)?.name)
        assertEquals("", leagues?.get(1)?.id)  // null converted to empty string
        assertEquals("", leagues?.get(2)?.name)  // null converted to empty string
        assertEquals("", leagues?.get(3)?.sport)  // null converted to empty string
    }

    @Test
    fun `getAllLeagues should return empty list when response leagues is null`() = runTest {
        // Given
        val response = LeaguesResponse(leagues = null)
        coEvery { apiService.getAllLeagues() } returns response

        // When
        val result = repository.getAllLeagues()

        // Then
        assertTrue(result.isSuccess)
        val leagues = result.getOrNull()
        assertEquals(0, leagues?.size)
    }

    @Test
    fun `getAllLeagues should return failure when API call throws exception`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { apiService.getAllLeagues() } throws exception

        // When
        val result = repository.getAllLeagues()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getTeamsByLeague should return success with teams when API call succeeds`() = runTest {
        // Given
        val teamDtos = listOf(
            TeamDto(id = "1", name = "Paris Saint-Germain", badgeUrl = "badge1.png", leagueName = "French Ligue 1"),
            TeamDto(id = "2", name = "Olympique Marseille", badgeUrl = "badge2.png", leagueName = "French Ligue 1")
        )
        val response = TeamsResponse(teams = teamDtos)
        coEvery { apiService.getTeamsByLeague("French Ligue 1") } returns response

        // When
        val result = repository.getTeamsByLeague("French Ligue 1")

        // Then
        assertTrue(result.isSuccess)
        val teams = result.getOrNull()
        assertEquals(2, teams?.size)
        assertEquals("Paris Saint-Germain", teams?.get(0)?.name)
        assertEquals("badge1.png", teams?.get(0)?.badgeUrl)
        coVerify(exactly = 1) { apiService.getTeamsByLeague("French Ligue 1") }
    }

    @Test
    fun `getTeamsByLeague should map null badgeUrl to empty string`() = runTest {
        // Given
        val teamDtos = listOf(
            TeamDto(id = "1", name = "Team A", badgeUrl = null, leagueName = "League A")
        )
        val response = TeamsResponse(teams = teamDtos)
        coEvery { apiService.getTeamsByLeague("League A") } returns response

        // When
        val result = repository.getTeamsByLeague("League A")

        // Then
        assertTrue(result.isSuccess)
        val teams = result.getOrNull()
        assertEquals(1, teams?.size)
        assertEquals("", teams?.get(0)?.badgeUrl)
    }

    @Test
    fun `getTeamsByLeague should convert null fields to empty strings`() = runTest {
        // Given
        val teamDtos = listOf(
            TeamDto(id = "1", name = "Valid Team", badgeUrl = "badge.png", leagueName = "League"),
            TeamDto(id = null, name = "Team with null id", badgeUrl = "badge.png", leagueName = "League"),
            TeamDto(id = "3", name = null, badgeUrl = "badge.png", leagueName = "League"),
            TeamDto(id = "4", name = "Team with null league", badgeUrl = "badge.png", leagueName = null)
        )
        val response = TeamsResponse(teams = teamDtos)
        coEvery { apiService.getTeamsByLeague("League") } returns response

        // When
        val result = repository.getTeamsByLeague("League")

        // Then
        assertTrue(result.isSuccess)
        val teams = result.getOrNull()
        assertEquals(4, teams?.size)
        assertEquals("Valid Team", teams?.get(0)?.name)
        assertEquals("", teams?.get(1)?.id)  // null converted to empty string
        assertEquals("", teams?.get(2)?.name)  // null converted to empty string
        assertEquals("", teams?.get(3)?.league)  // null converted to empty string
    }

    @Test
    fun `getTeamsByLeague should return empty list when response teams is null`() = runTest {
        // Given
        val response = TeamsResponse(teams = null)
        coEvery { apiService.getTeamsByLeague("League") } returns response

        // When
        val result = repository.getTeamsByLeague("League")

        // Then
        assertTrue(result.isSuccess)
        val teams = result.getOrNull()
        assertEquals(0, teams?.size)
    }

    @Test
    fun `getTeamsByLeague should return failure when API call throws exception`() = runTest {
        // Given
        val exception = Exception("API error")
        coEvery { apiService.getTeamsByLeague("League") } throws exception

        // When
        val result = repository.getTeamsByLeague("League")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
