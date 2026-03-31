package com.jeromedusanter.fdjtest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jeromedusanter.fdjtest.ui.screen.leaguesearch.LeagueSearchScreen
import com.jeromedusanter.fdjtest.ui.screen.teamslist.TeamsListScreen

@Composable
fun FdjNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LeagueSearchDestination,
        modifier = modifier
    ) {
        composable<LeagueSearchDestination> {
            LeagueSearchScreen(
                onLeagueSelected = { leagueName ->
                    navController.navigate(TeamsListDestination(leagueName = leagueName))
                }
            )
        }

        composable<TeamsListDestination> { backStackEntry ->
            val teamsListDestination = backStackEntry.toRoute<TeamsListDestination>()
            TeamsListScreen(
                leagueName = teamsListDestination.leagueName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
