package com.jeromedusanter.fdjtest.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jeromedusanter.fdjtest.ui.navigation.FdjNavHost

@Composable
fun FdjTestApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    FdjNavHost(
        navController = navController,
        modifier = modifier.fillMaxSize()
    )
}