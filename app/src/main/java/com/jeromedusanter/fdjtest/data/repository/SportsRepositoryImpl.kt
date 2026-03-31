package com.jeromedusanter.fdjtest.data.repository

import com.jeromedusanter.fdjtest.data.api.SportsApiService
import com.jeromedusanter.fdjtest.data.model.LeagueDto
import com.jeromedusanter.fdjtest.data.model.TeamDto
import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Result
import com.jeromedusanter.fdjtest.domain.model.Team
import com.jeromedusanter.fdjtest.domain.repository.SportsRepository
import javax.inject.Inject
import kotlin.coroutines.Coroutine Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SportsRepositoryImpl @Inject constructor(
    private val apiService: SportsApiService,
    private val ioDispatcher: CoroutineDispatcher
) : SportsRepository {

    override suspend fun getAllLeagues(): Result<List<League>> = withContext(ioDispatcher) {
        try {
            val response = apiService.getAllLeagues()
            val leagues = response.leagues?.mapNotNull { it.toDomainModel() } ?: emptyList()
            Result.Success(leagues)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTeamsByLeague(leagueName: String): Result<List<Team>> = withContext(ioDispatcher) {
        try {
            val response = apiService.getTeamsByLeague(leagueName)
            val teams = response.teams?.mapNotNull { it.toDomainModel() } ?: emptyList()
            Result.Success(teams)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun LeagueDto.toDomainModel(): League? {
        val id = idLeague ?: return null
        val name = strLeague ?: return null
        val sport = strSport ?: return null
        return League(id = id, name = name, sport = sport)
    }

    private fun TeamDto.toDomainModel(): Team? {
        val id = idTeam ?: return null
        val name = strTeam ?: return null
        val badgeUrl = strTeamBadge ?: return null
        val league = strLeague ?: return null
        return Team(id = id, name = name, badgeUrl = badgeUrl, league = league)
    }
}
