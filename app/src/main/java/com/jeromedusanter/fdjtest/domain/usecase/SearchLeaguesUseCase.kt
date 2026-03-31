package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import javax.inject.Inject

class SearchLeaguesUseCase @Inject constructor(
    private val repository: SportsRepository
) {
    suspend operator fun invoke(query: String): Result<List<League>> {
        return repository.getAllLeagues().map { leagues ->
            if (query.isBlank()) {
                leagues
            } else {
                leagues.filter { league ->
                    league.name.contains(query, ignoreCase = true)
                }
            }
        }
    }
}
