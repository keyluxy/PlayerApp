package com.example.playerapp.presentation.screen

import CustomBottomNavigation
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.playerapp.presentation.navigation.MusicAppNavigation
import com.example.playerapp.presentation.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                navController = navController,
                currentRoute = currentRoute,
                screens = listOf(
                    Screen.TrackScreen,
                    Screen.DownloadedTrackScreen
                )
            )
        }
    ) { padding ->
        MusicAppNavigation(
            startDestination = Screen.TrackScreen.route,
            navController = navController,
            padding = padding
        )
    }
}