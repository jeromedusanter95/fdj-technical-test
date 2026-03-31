package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Result
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchLeaguesUseCaseTest {

    private lateinit var repository: SportsRepository
    private lateinit var useCase: SearchLeaguesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchLeaguesUseCase(repository)
    }

    @Test
    fun `invoke should return all leagues when query is blank`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer"),
            League("3", "NBA", "Basketball")
        )
        coEvery { repository.getAllLeagues() } returns Result.Success(leagues)

        val result = useCase.invoke("")

        assertTrue(result is Result.Success)
        assertEquals(3, (result as Result.Success).data.size)
        assertEquals(leagues, result.data)

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should filter leagues by query case insensitive`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer"),
            League("3", "NBA", "Basketball"),
            League("4", "Champions League", "Soccer")
        )
        coEvery { repository.getAllLeagues() } returns Result.Success(leagues)

        val result = useCase.invoke("league")

        assertTrue(result is Result.Success)
        val filteredLeagues = (result as Result.Success).data

        assertEquals(2, filteredLeagues.size)
        assertTrue(filteredLeagues.any { it.name == "English Premier League" })
        assertTrue(filteredLeagues.any { it.name == "Champions League" })

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should handle uppercase query`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer"),
            League("3", "NBA", "Basketball")
        )
        coEvery { repository.getAllLeagues() } returns Result.Success(leagues)

        val result = useCase.invoke("ENGLISH")

        assertTrue(result is Result.Success)
        val filteredLeagues = (result as Result.Success).data

        assertEquals(1, filteredLeagues.size)
        assertEquals("English Premier League", filteredLeagues[0].name)
    }

    @Test
    fun `invoke should return empty list when no matches found`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer")
        )
        coEvery { repository.getAllLeagues() } returns Result.Success(leagues)

        val result = useCase.invoke("xyz")

        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getAllLeagues() } returns Result.Error(exception)

        val result = useCase.invoke("test")

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should handle whitespace query`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer")
        )
        coEvery { repository.getAllLeagues() } returns Result.Success(leagues)

        val result = useCase.invoke("   ")

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }
}
