package com.example.playerapp.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.playerapp.R
import com.example.playerapp.data.models.Track
import com.example.playerapp.presentation.navigation.Screen
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import com.example.playerapp.presentation.viewmodel.TrackViewModel
import kotlinx.coroutines.launch

@Composable
fun TrackScreen(
    navController: NavController,
    trackViewModel: TrackViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel
) {
    LaunchedEffect(Unit) {
        trackViewModel.downloadCompleted.collect {
            playerViewModel.setLocalTracks(trackViewModel.uiState.value.tracks)
        }
    }

    val uiState by trackViewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(onSearch = { query -> trackViewModel.searchTracks(query) })

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState.error != null) {
            Text(text = uiState.error ?: "Unknown Error", color = Color.Red)
        } else {
            TrackList(
                tracks = uiState.tracks,
                onItemClick = { track ->
                    playerViewModel.setLocalTracks(uiState.tracks)
                    playerViewModel.playTrack(track)
                    navController.navigate(Screen.PlaybackScreen.route)
                },
                onDownloadClick = { track ->
                    trackViewModel.downloadTrack(track)
                }
            )
        }
    }
}


@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }

    TextField(
        value = query,
        onValueChange = { query = it },
        label = { Text("Search Tracks") },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { onSearch(query) }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}


@Composable
fun TrackList(
    tracks: List<Track>,
    onItemClick: (Track) -> Unit,
    onDownloadClick: ((Track) -> Unit)? = null,
    onDeleteClick: ((Track) -> Unit)? = null
) {
    LazyColumn {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onItemClick = { onItemClick(track) },
                onDownloadClick = onDownloadClick?.let { { it(track) } },
                onDeleteClick = onDeleteClick?.let { { it(track) } }
            )
        }
    }
}


@Composable
fun TrackItem(
    track: Track,
    onItemClick: () -> Unit,
    onDownloadClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var showActionDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val hasCover = !track.album.cover.isNullOrEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(16.dp)
    ) {
        if (hasCover) {
            AsyncImage(
                model = track.album.cover?.replace("{w}", "500")?.replace("{h}", "500"),
                contentDescription = "Track Cover",
                modifier = Modifier.size(64.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_default),
                contentDescription = "Default Cover",
                modifier = Modifier.size(64.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(text = track.title, fontWeight = FontWeight.Bold)
            Text(text = track.artist.name, style = MaterialTheme.typography.labelSmall)
        }

        IconButton(onClick = { showActionDialog = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Действия")
        }
    }

    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Выберите действие") },
            text = { },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onDownloadClick?.let { action ->
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    action()
                                    showActionDialog = false
                                }
                            }
                        ) {
                            Text("Скачать")
                        }
                    }

                    onDeleteClick?.let { action ->
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    action()
                                    showActionDialog = false
                                }
                            }
                        ) {
                            Text("Удалить", color = Color.Red)
                        }
                    }

                    TextButton(
                        onClick = { showActionDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }
            }
        )
    }
}

