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
        startDestination = LeagueSearchScreen,
        modifier = modifier
    ) {
        composable<LeagueSearchScreen> {
            LeagueSearchScreen(
                onLeagueSelected = { leagueName ->
                    navController.navigate(TeamsListScreen(leagueName = leagueName))
                }
            )
        }

        composable<TeamsListScreen> { backStackEntry ->
            val teamsListScreen = backStackEntry.toRoute<TeamsListScreen>()
            TeamsListScreen(
                leagueName = teamsListScreen.leagueName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
