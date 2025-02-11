package com.example.playerapp.presentation.navigation.custom_bottom_nav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playerapp.presentation.navigation.Screen

@Composable
fun CustomBottomNavigationItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
//            .weight(1f)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Выбранный элемент
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (screen) {
                        Screen.TrackScreen -> Icons.Default.Home
                        Screen.DownloadedTrackScreen -> Icons.Default.Home
                        Screen.PlaybackScreen -> Icons.Default.Home
                    },
                    contentDescription = null,
                    tint = Color.Blue
                )
                Text(
                    text = stringResource(id = screen.label),
                    color = Color.Blue,
                    fontSize = 10.sp
                )
            }
        } else {
            // Не выбранный элемент
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (screen) {
                        Screen.TrackScreen -> Icons.Default.Home
                        Screen.DownloadedTrackScreen -> Icons.Default.Home
                        Screen.PlaybackScreen -> Icons.Default.Home
                    },
                    contentDescription = null,
                    tint = Color.Gray
                )
                Text(
                    text = stringResource(id = screen.label),
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}