package com.jeromedusanter.fdjtest.domain.repository

import com.jeromedusanter.fdjtest.domain.model.League
import com.jeromedusanter.fdjtest.domain.model.Team

interface SportsRepository {

    suspend fun getAllLeagues(): Result<List<League>>

    suspend fun getTeamsByLeague(leagueName: String): Result<List<Team>>
}
