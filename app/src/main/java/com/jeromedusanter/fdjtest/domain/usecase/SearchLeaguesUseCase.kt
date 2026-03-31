package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Result
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import javax.inject.Inject

class SearchLeaguesUseCase @Inject constructor(
    private val repository: SportsRepository
) {
    suspend operator fun invoke(query: String): Result<List<League>> {
        return when (val result = repository.getAllLeagues()) {
            is Result.Success -> {
                val filteredLeagues = if (query.isBlank()) {
                    result.data
                } else {
                    result.data.filter { league ->
                        league.name.contains(query, ignoreCase = true)
                    }
                }
                Result.Success(filteredLeagues)
            }
            is Result.Error -> result
        }
    }
}
