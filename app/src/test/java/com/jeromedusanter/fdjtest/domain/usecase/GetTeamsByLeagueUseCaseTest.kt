package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTeamsByLeagueUseCaseTest {

    private lateinit var repository: SportsRepository
    private lateinit var useCase: GetTeamsByLeagueUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetTeamsByLeagueUseCase(repository)
    }

    @Test
    fun `invoke should filter every other team then sort in reverse alphabetical order`() = runTest {
        // Given
        val teams = listOf(
            Team("1", "Arsenal", "badge1.png", "English Premier League"),
            Team("2", "Barcelona", "badge2.png", "Spanish La Liga"),
            Team("3", "Chelsea", "badge3.png", "English Premier League"),
            Team("4", "Dortmund", "badge4.png", "German Bundesliga"),
            Team("5", "Everton", "badge5.png", "English Premier League"),
            Team("6", "Fiorentina", "badge6.png", "Italian Serie A")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.success(teams)

        // When
        val result = useCase.invoke("test league")

        // Then
        // Filter: indices 0,2,4 → Arsenal, Chelsea, Everton
        // Sort descending: Everton, Chelsea, Arsenal
        assertTrue(result.isSuccess)
        val processedTeams = result.getOrNull()
        assertEquals(3, processedTeams?.size)
        assertEquals("Everton", processedTeams?.get(0)?.name)
        assertEquals("Chelsea", processedTeams?.get(1)?.name)
        assertEquals("Arsenal", processedTeams?.get(2)?.name)
        coVerify(exactly = 1) { repository.getTeamsByLeague("test league") }
    }

    @Test
    fun `invoke should handle single team correctly`() = runTest {
        // Given
        val teams = listOf(
            Team("1", "Arsenal", "badge1.png", "English Premier League")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.success(teams)

        // When
        val result = useCase.invoke("test league")

        // Then
        assertTrue(result.isSuccess)
        val processedTeams = result.getOrNull()
        assertEquals(1, processedTeams?.size)
        assertEquals("Arsenal", processedTeams?.get(0)?.name)
    }

    @Test
    fun `invoke should handle two teams correctly`() = runTest {
        // Given
        val teams = listOf(
            Team("1", "Arsenal", "badge1.png", "English Premier League"),
            Team("2", "Barcelona", "badge2.png", "Spanish La Liga")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.success(teams)

        // When
        val result = useCase.invoke("test league")

        // Then
        // Filter: index 0 → Arsenal
        // Sort: Arsenal (single item)
        assertTrue(result.isSuccess)
        val processedTeams = result.getOrNull()
        assertEquals(1, processedTeams?.size)
        assertEquals("Arsenal", processedTeams?.get(0)?.name)
    }

    @Test
    fun `invoke should handle empty list`() = runTest {
        // Given
        coEvery { repository.getTeamsByLeague("test league") } returns Result.success(emptyList())

        // When
        val result = useCase.invoke("test league")

        // Then
        assertTrue(result.isSuccess)
        val processedTeams = result.getOrNull()
        assertEquals(0, processedTeams?.size)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { repository.getTeamsByLeague("test league") } returns Result.failure(exception)

        // When
        val result = useCase.invoke("test league")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.getTeamsByLeague("test league") }
    }

    @Test
    fun `invoke should verify filtering keeps indices 0, 2, 4 after sorting`() = runTest {
        // Given
        val teams = listOf(
            Team("1", "A Team", "badge1.png", "League"),
            Team("2", "B Team", "badge2.png", "League"),
            Team("3", "C Team", "badge3.png", "League"),
            Team("4", "D Team", "badge4.png", "League"),
            Team("5", "E Team", "badge5.png", "League")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.success(teams)

        // When
        val result = useCase.invoke("test league")

        // Then
        assertTrue(result.isSuccess)
        val processedTeams = result.getOrNull()
        assertEquals(3, processedTeams?.size)
        assertEquals("E Team", processedTeams?.get(0)?.name)
        assertEquals("C Team", processedTeams?.get(1)?.name)
        assertEquals("A Team", processedTeams?.get(2)?.name)
    }
}
