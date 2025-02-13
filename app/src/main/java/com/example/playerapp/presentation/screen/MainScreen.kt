package com.example.playerapp.presentation.screen

import CustomBottomNavigation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.playerapp.presentation.navigation.MusicAppNavigation
import com.example.playerapp.presentation.navigation.Screen
import com.example.playerapp.presentation.screen.player.MiniPlayer
import com.example.playerapp.presentation.viewmodel.PlayerViewModel


@Composable
fun MainScreen(playerViewModel: PlayerViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTrack by playerViewModel.currentTrack.collectAsState()

    Scaffold(
        bottomBar = {
            Column {
                if (currentTrack != null && currentRoute != Screen.PlaybackScreen.route) {
                    MiniPlayer(
                        playerViewModel = playerViewModel,
                        navController = navController,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CustomBottomNavigation(
                    navController = navController,
                    currentRoute = currentRoute,
                    screens = listOf(
                        Screen.TrackScreen,
                        Screen.DownloadedTrackScreen
                    )
                )
            }
        }
    ) { padding ->
        MusicAppNavigation(
            startDestination = Screen.TrackScreen.route,
            navController = navController,
            padding = padding,
            playerViewModel = playerViewModel
        )
    }
}








