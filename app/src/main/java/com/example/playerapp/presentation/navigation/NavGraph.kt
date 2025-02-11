package com.example.playerapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playerapp.R
import com.example.playerapp.presentation.screen.TrackScreen

sealed class Screen(val route: String, val label: Int) {

    data object TrackScreen : Screen("track_screen", R.string.track_screen_label)
    data object DownloadedTrackScreen : Screen("downloaded_track_screen",  R.string.downloaded_screen_label)
    data object PlaybackScreen : Screen("playback_screen/{trackId}", R.string.playback_screen_label) {
        fun createRoute(trackId: Int) = "playback_screen/$trackId"
    }
}

@Composable
fun MusicAppNavigation(
    startDestination: String,
    navController: NavHostController, // Добавляем navController как параметр
    padding: PaddingValues // Добавляем padding как параметр
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(padding) // Применяем padding к NavHost
    ) {
        composable(Screen.TrackScreen.route) {
            TrackScreen()
        }
        composable(Screen.DownloadedTrackScreen.route) {
            // DownloadedTrackScreen() // Здесь можно добавить экран скачанных треков
        }
        composable(Screen.PlaybackScreen.route) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId")?.toIntOrNull() ?: -1
            // PlaybackScreen(trackId = trackId) // Здесь можно добавить экран воспроизведения
        }
    }
}