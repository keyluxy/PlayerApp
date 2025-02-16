package com.example.playerapp.presentation.screen.player

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.playerapp.R
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun PlaybackScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel
) {
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val progress by playerViewModel.progress.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()

    var isVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 2000f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = offsetY.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    isVisible = false
                    coroutineScope.launch {
                        delay(500)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier.size(32.dp)
                )
            }

            currentTrack?.let { track ->
                AsyncImage(
                    model = track.album.cover,
                    contentDescription = "Album cover",
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = track.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = track.artist.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                Slider(
                    value = progress.toFloat(),
                    onValueChange = { newValue ->
                        playerViewModel.seekTo(newValue.toInt())
                    },
                    valueRange = 0f..playerViewModel.trackDuration.toFloat()
                )


                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = playerViewModel.formatTime(progress))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = playerViewModel.formatTime(playerViewModel.trackDuration))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { playerViewModel.previousTrack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_previous),
                            contentDescription = "Previous"
                        )
                    }

                    IconButton(
                        onClick = { playerViewModel.togglePlayPause() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(onClick = { playerViewModel.nextTrack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next),
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    }
}