package com.example.playerapp.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.playerapp.data.database.toTrack
import com.example.playerapp.presentation.navigation.Screen
import com.example.playerapp.presentation.viewmodel.DownloadedTrackViewModel
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import com.example.playerapp.presentation.viewmodel.intent.DownloadedTrackIntent
import com.example.playerapp.presentation.viewmodel.state.DownloadedTrackState

@Composable
fun DownloadedTrackScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    downloadedTrackViewModel: DownloadedTrackViewModel = hiltViewModel()
) {
    val uiState by downloadedTrackViewModel.state.collectAsState(initial = DownloadedTrackState())

    LaunchedEffect(Unit) {
        downloadedTrackViewModel.sendIntent(DownloadedTrackIntent.RefreshDownloads)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState.error != null) {
            Text(text = uiState.error ?: "Unknown Error", color = Color.Red)
        } else {
            TrackList(
                tracks = uiState.tracks.map { it.toTrack() }, // Преобразуем DownloadedTrackEntity в Track
                onItemClick = { track ->
                    playerViewModel.setLocalTracks(uiState.tracks.map { it.toTrack() })
                    playerViewModel.playTrack(track)
                    navController.navigate(Screen.PlaybackScreen.route)
                },
                onDeleteClick = { track ->
                    val trackEntity = uiState.tracks.find { it.id.toString() == track.id.toString() }
                    if (trackEntity != null) {
                        downloadedTrackViewModel.sendIntent(
                            DownloadedTrackIntent.DeleteTrack(trackEntity)
                        )
                    }
                }
            )

        }
    }
}



