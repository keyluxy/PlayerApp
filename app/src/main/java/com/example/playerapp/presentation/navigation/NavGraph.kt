package com.example.playerapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playerapp.R
import com.example.playerapp.presentation.screen.TrackScreen
import com.example.playerapp.presentation.screen.downloadedtrack.DownloadedTrackScreen
import com.example.playerapp.presentation.screen.player.PlaybackScreen
import com.example.playerapp.presentation.viewmodel.DownloadedTrackViewModel
import com.example.playerapp.presentation.viewmodel.PlayerViewModel

sealed class Screen(val route: String, val label: Int) {

    data object TrackScreen : Screen("track_screen", R.string.track_screen_label)
    data object DownloadedTrackScreen : Screen("downloaded_track_screen",  R.string.downloaded_screen_label)
    data object PlaybackScreen : Screen("playback", R.string.playback)

}

@Composable
fun MusicAppNavigation(
    startDestination: String,
    navController: NavHostController,
    padding: PaddingValues,
    playerViewModel: PlayerViewModel,
    downloadedTrackViewModel: DownloadedTrackViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(padding)
    ) {
        composable(Screen.TrackScreen.route) {
            TrackScreen(
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
        composable(Screen.DownloadedTrackScreen.route) {
            DownloadedTrackScreen(
                navController = navController,
                playerViewModel = playerViewModel,
                trackViewModel = hiltViewModel(),
                downloadedTrackViewModel = hiltViewModel()
            )
        }
        composable(Screen.PlaybackScreen.route) {
            PlaybackScreen(
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
    }
}
