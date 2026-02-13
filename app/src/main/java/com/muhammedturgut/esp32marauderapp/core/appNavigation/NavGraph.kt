package com.muhammedturgut.esp32marauderapp.core.appNavigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.muhammedturgut.esp32marauderapp.presentation.view.MainScreen
import com.muhammedturgut.esp32marauderapp.presentation.view.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "SplashScreen",
        modifier = modifier
    ) {

        composable("SplashScreen") {
            SplashScreen(navController = navController)
        }

        composable("HomeScreen") {
            MainScreen()
        }
    }
}
