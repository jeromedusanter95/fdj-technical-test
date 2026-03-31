package com.jeromedusanter.fdjtest.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LeagueSearchDestination

@Serializable
data class TeamsListDestination(val leagueName: String)
