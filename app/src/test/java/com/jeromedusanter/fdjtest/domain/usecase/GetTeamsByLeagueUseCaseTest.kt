package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.Result
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
    fun `invoke should sort teams in reverse alphabetical order and filter every other team`() = runTest {
        val teams = listOf(
            Team("1", "Arsenal", "badge1.png", "English Premier League"),
            Team("2", "Barcelona", "badge2.png", "Spanish La Liga"),
            Team("3", "Chelsea", "badge3.png", "English Premier League"),
            Team("4", "Dortmund", "badge4.png", "German Bundesliga"),
            Team("5", "Everton", "badge5.png", "English Premier League"),
            Team("6", "Fiorentina", "badge6.png", "Italian Serie A")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.Success(teams)

        val result = useCase.invoke("test league")

        assertTrue(result is Result.Success)
        val processedTeams = (result as Result.Success).data

        assertEquals(3, processedTeams.size)
        assertEquals("Fiorentina", processedTeams[0].name)
        assertEquals("Dortmund", processedTeams[1].name)
        assertEquals("Barcelona", processedTeams[2].name)

        coVerify(exactly = 1) { repository.getTeamsByLeague("test league") }
    }

    @Test
    fun `invoke should handle single team correctly`() = runTest {
        val teams = listOf(
            Team("1", "Arsenal", "badge1.png", "English Premier League")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.Success(teams)

        val result = useCase.invoke("test league")

        assertTrue(result is Result.Success)
        val processedTeams = (result as Result.Success).data

        assertEquals(1, processedTeams.size)
        assertEquals("Arsenal", processedTeams[0].name)
    }

    @Test
    fun `invoke should handle two teams correctly`() = runTest {
        val teams = listOf(
            Team("1", "Arsenal", "badge1.png", "English Premier League"),
            Team("2", "Barcelona", "badge2.png", "Spanish La Liga")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.Success(teams)

        val result = useCase.invoke("test league")

        assertTrue(result is Result.Success)
        val processedTeams = (result as Result.Success).data

        assertEquals(1, processedTeams.size)
        assertEquals("Barcelona", processedTeams[0].name)
    }

    @Test
    fun `invoke should handle empty list`() = runTest {
        coEvery { repository.getTeamsByLeague("test league") } returns Result.Success(emptyList())

        val result = useCase.invoke("test league")

        assertTrue(result is Result.Success)
        val processedTeams = (result as Result.Success).data

        assertEquals(0, processedTeams.size)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getTeamsByLeague("test league") } returns Result.Error(exception)

        val result = useCase.invoke("test league")

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)

        coVerify(exactly = 1) { repository.getTeamsByLeague("test league") }
    }

    @Test
    fun `invoke should verify filtering keeps indices 0, 2, 4 after sorting`() = runTest {
        val teams = listOf(
            Team("1", "A Team", "badge1.png", "League"),
            Team("2", "B Team", "badge2.png", "League"),
            Team("3", "C Team", "badge3.png", "League"),
            Team("4", "D Team", "badge4.png", "League"),
            Team("5", "E Team", "badge5.png", "League")
        )
        coEvery { repository.getTeamsByLeague("test league") } returns Result.Success(teams)

        val result = useCase.invoke("test league")

        assertTrue(result is Result.Success)
        val processedTeams = (result as Result.Success).data

        assertEquals(3, processedTeams.size)
        assertEquals("E Team", processedTeams[0].name)
        assertEquals("C Team", processedTeams[1].name)
        assertEquals("A Team", processedTeams[2].name)
    }
}
