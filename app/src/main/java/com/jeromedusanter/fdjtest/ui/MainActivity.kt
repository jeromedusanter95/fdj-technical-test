package com.jeromedusanter.fdjtest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.fdjtest.ui.navigation.FdjNavHost
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

    FdjNavHost(
        navController = navController,
        modifier = modifier.fillMaxSize()
    )
}