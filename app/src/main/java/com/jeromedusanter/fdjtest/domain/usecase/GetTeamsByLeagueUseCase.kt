package com.jeromedusanter.fdjtest.domain.usecase

import com.jeromedusanter.fdjtest.domain.model.Result
import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import javax.inject.Inject

class GetTeamsByLeagueUseCase @Inject constructor(
    private val repository: SportsRepository
) {
    suspend operator fun invoke(leagueName: String): Result<List<Team>> {
        return when (val result = repository.getTeamsByLeague(leagueName)) {
            is Result.Success -> {

                println(result.data)

                val processedTeams = result.data
                    .sortedByDescending { it.name }
                    .filterIndexed { index, _ -> index % 2 == 0 }


                println(processedTeams)

                Result.Success(processedTeams)
            }
            is Result.Error -> result
        }
    }
}
