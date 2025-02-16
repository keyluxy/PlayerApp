package com.example.playerapp.presentation.screen.downloadedtrack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.playerapp.data.models.Album
import com.example.playerapp.data.models.Artist
import com.example.playerapp.data.models.Track
import com.example.playerapp.presentation.navigation.Screen
import com.example.playerapp.presentation.screen.SearchBar
import com.example.playerapp.presentation.screen.TrackList
import com.example.playerapp.presentation.viewmodel.DownloadedTrackViewModel
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import com.example.playerapp.presentation.viewmodel.TrackViewModel

@Composable
fun DownloadedTrackScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    trackViewModel: TrackViewModel,
    downloadedTrackViewModel: DownloadedTrackViewModel
) {
    val uiState by downloadedTrackViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        trackViewModel.downloadCompleted.collect {
            downloadedTrackViewModel.refreshDownloads()
        }
    }

    val tracks = uiState.tracks.map { downloadedTrack ->
        Track(
            id = downloadedTrack.id.toLongOrNull() ?: 0L,
            title = downloadedTrack.title,
            artist = Artist(
                id = 0,
                name = downloadedTrack.artist,
                picture = downloadedTrack.coverUrl
            ),
            album = Album(
                id = 0,
                title = downloadedTrack.title,
                cover = downloadedTrack.coverUrl
            ),
            preview = downloadedTrack.url,
            position = 0,
            localFilePath = downloadedTrack.localFilePath
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TrackList(
            tracks = tracks,
            onItemClick = { track ->
                playerViewModel.setLocalTracks(tracks)
                playerViewModel.playTrack(track.copy(localFilePath = track.localFilePath))
                navController.navigate(Screen.PlaybackScreen.route)
            },
            onDeleteClick = { track ->
                uiState.tracks.find { it.id == track.id.toString() }?.let {
                    downloadedTrackViewModel.deleteTrack(it)
                }
            }
        )
    }
}

