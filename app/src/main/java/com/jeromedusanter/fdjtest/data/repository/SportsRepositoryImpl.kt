package com.jeromedusanter.fdjtest.data.repository

import com.jeromedusanter.fdjtest.data.api.SportsApiService
import com.jeromedusanter.fdjtest.data.model.LeagueDto
import com.jeromedusanter.fdjtest.data.model.TeamDto
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Result
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
        try {
            val response = apiService.getAllLeagues()
            val leagues = response.leagues?.map { it.toDomainModel() }.orEmpty()
            Result.Success(leagues)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTeamsByLeague(leagueName: String): Result<List<Team>> = withContext(ioDispatcher) {
        try {
            val response = apiService.getTeamsByLeague(leagueName)
            val teams = response.teams?.map { it.toDomainModel() }.orEmpty()
            Result.Success(teams)
        } catch (e: Exception) {
            Result.Error(e)
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
