package com.jeromedusanter.fdjtest.data.repository

import com.jeromedusanter.fdjtest.data.api.SportsApiService
import com.jeromedusanter.fdjtest.data.model.LeagueDto
import com.jeromedusanter.fdjtest.data.model.TeamDto
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import com.jeromedusanter.fdjtest.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SportsRepositoryImpl @Inject constructor(
    private val apiService: SportsApiService,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SportsRepository {

    override suspend fun getAllLeagues(): Result<List<League>> = withContext(ioDispatcher) {
        runCatching {
            val response = apiService.getAllLeagues()
            response.leagues?.map { it.toDomainModel() }.orEmpty()
        }
    }

    override suspend fun getTeamsByLeague(leagueName: String): Result<List<Team>> = withContext(ioDispatcher) {
        runCatching {
            val response = apiService.getTeamsByLeague(leagueName)
            response.teams?.map { it.toDomainModel() }.orEmpty()
        }
    }

    private fun LeagueDto.toDomainModel(): League {
        val id = id.orEmpty()
        val name = name.orEmpty()
        val sport = sport.orEmpty()
        return League(id = id, name = name, sport = sport)
    }

    private fun TeamDto.toDomainModel(): Team {
        val id = id.orEmpty()
        val name = name.orEmpty()
        val badgeUrl = badgeUrl.orEmpty()
        val league = leagueName.orEmpty()
        return Team(id = id, name = name, badgeUrl = badgeUrl, league = league)
    }
}
