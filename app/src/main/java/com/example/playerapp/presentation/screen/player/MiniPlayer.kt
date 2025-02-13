package com.example.playerapp.presentation.screen.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.playerapp.R
import com.example.playerapp.presentation.navigation.Screen
import com.example.playerapp.presentation.viewmodel.PlayerViewModel

@Composable
fun MiniPlayer(
    playerViewModel: PlayerViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()

    if (currentTrack != null && isPlaying) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.PlaybackScreen.route) }
                .padding(8.dp)
        ) {
            AsyncImage(
                model = currentTrack!!.album.cover,
                contentDescription = "Track Cover",
                modifier = Modifier.size(48.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = currentTrack!!.title, fontWeight = FontWeight.Bold)
                Text(text = currentTrack!!.artist.name, style = MaterialTheme.typography.labelSmall)
            }
            Row {
                IconButton(onClick = { playerViewModel.previousTrack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_previous),
                        contentDescription = "Previous",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { playerViewModel.togglePlayPause() }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { playerViewModel.nextTrack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}





