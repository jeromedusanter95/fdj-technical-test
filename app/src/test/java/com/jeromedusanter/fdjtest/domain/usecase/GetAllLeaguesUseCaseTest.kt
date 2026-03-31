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

class GetAllLeaguesUseCaseTest {

    private lateinit var repository: SportsRepository
    private lateinit var useCase: GetAllLeaguesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllLeaguesUseCase(repository)
    }

    @Test
    fun `invoke should return success with leagues from repository`() = runTest {
        val leagues = listOf(
            League("1", "English Premier League", "Soccer"),
            League("2", "Spanish La Liga", "Soccer"),
            League("3", "NBA", "Basketball")
        )
        coEvery { repository.getAllLeagues() } returns Result.success(leagues)

        val result = useCase.invoke()

        assertTrue(result.isSuccess)
        assertEquals(leagues, result.getOrNull())

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should return empty list when repository returns empty`() = runTest {
        coEvery { repository.getAllLeagues() } returns Result.success(emptyList())

        val result = useCase.invoke()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getAllLeagues() } returns Result.failure(exception)

        val result = useCase.invoke()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }
}
