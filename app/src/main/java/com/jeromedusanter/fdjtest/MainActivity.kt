package com.jeromedusanter.fdjtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jeromedusanter.fdjtest.ui.navigation.LeagueSearchScreen
import com.jeromedusanter.fdjtest.ui.navigation.TeamsListScreen
import com.jeromedusanter.fdjtest.ui.screen.teamslist.TeamsListScreen as TeamsListScreenComposable
import com.jeromedusanter.fdjtest.ui.screen.leaguesearch.LeagueSearchScreen as LeagueSearchScreenComposable
import com.jeromedusanter.fdjtest.ui.theme.FDJTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FDJTestTheme {
                FdjTestApp()
            }
        }
    }
}

@Composable
fun FdjTestApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LeagueSearchScreen,
        modifier = modifier.fillMaxSize()
    ) {
        composable<LeagueSearchScreen> {
            LeagueSearchScreenComposable(
                onLeagueSelected = { leagueName ->
                    navController.navigate(TeamsListScreen(leagueName = leagueName))
                }
            )
        }

        composable<TeamsListScreen> {
            TeamsListScreenComposable(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}