package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import javax.inject.Inject

class GetTeamsByLeagueUseCase @Inject constructor(
    private val repository: SportsRepository
) {
    suspend operator fun invoke(leagueName: String): Result<List<Team>> {
        return repository.getTeamsByLeague(leagueName).map { teams ->
            teams
                .filterIndexed { index, _ -> index % 2 == 0 }
                .sortedByDescending { it.name }
        }
    }
}
