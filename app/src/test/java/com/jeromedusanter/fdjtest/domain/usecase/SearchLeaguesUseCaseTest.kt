package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.League
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
    fun `invoke should return empty list when query is blank`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer"),
            League("3", "NBA", "Basketball")
        )
        coEvery { repository.getAllLeagues() } returns Result.success(leagues)

        val result = useCase.invoke("")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)

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
        coEvery { repository.getAllLeagues() } returns Result.success(leagues)

        val result = useCase.invoke("league")

        assertTrue(result.isSuccess)
        val filteredLeagues = result.getOrNull()

        assertEquals(2, filteredLeagues?.size)
        assertTrue(filteredLeagues?.any { it.name == "English Premier League" } == true)
        assertTrue(filteredLeagues?.any { it.name == "Champions League" } == true)

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should handle uppercase query`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer"),
            League("3", "NBA", "Basketball")
        )
        coEvery { repository.getAllLeagues() } returns Result.success(leagues)

        val result = useCase.invoke("ENGLISH")

        assertTrue(result.isSuccess)
        val filteredLeagues = result.getOrNull()

        assertEquals(1, filteredLeagues?.size)
        assertEquals("English Premier League", filteredLeagues?.get(0)?.name)
    }

    @Test
    fun `invoke should return empty list when no matches found`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer")
        )
        coEvery { repository.getAllLeagues() } returns Result.success(leagues)

        val result = useCase.invoke("xyz")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getAllLeagues() } returns Result.failure(exception)

        val result = useCase.invoke("test")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should handle whitespace query`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer")
        )
        coEvery { repository.getAllLeagues() } returns Result.success(leagues)

        val result = useCase.invoke("   ")

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }
}
