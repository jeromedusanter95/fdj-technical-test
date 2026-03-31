package com.jeromedusanter.fdjtest.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LeagueSearchScreen

@Serializable
data class TeamsListScreen(val leagueName: String)
