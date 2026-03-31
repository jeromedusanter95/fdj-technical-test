package com.jeromedusanter.fdjtest.data.api

import com.jeromedusanter.fdjtest.data.model.LeaguesResponse
import com.jeromedusanter.fdjtest.data.model.TeamsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SportsApiService {

    @GET("all_leagues.php")
    suspend fun getAllLeagues(): LeaguesResponse

    @GET("search_all_teams.php")
    suspend fun getTeamsByLeague(@Query("l") leagueName: String): TeamsResponse
}
