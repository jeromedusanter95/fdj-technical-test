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
        coEvery { repository.getAllLeagues() } returns Result.Success(leagues)

        val result = useCase.invoke()

        assertTrue(result is Result.Success)
        assertEquals(leagues, (result as Result.Success).data)

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }

    @Test
    fun `invoke should return empty list when repository returns empty`() = runTest {
        coEvery { repository.getAllLeagues() } returns Result.Success(emptyList())

        val result = useCase.invoke()

        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getAllLeagues() } returns Result.Error(exception)

        val result = useCase.invoke()

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)

        coVerify(exactly = 1) { repository.getAllLeagues() }
    }
}
