package com.example.playerapp.presentation.navigation.custom_bottom_nav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playerapp.R
import com.example.playerapp.presentation.navigation.Screen

@Composable
fun CustomBottomNavigationItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(2.dp)
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (screen == Screen.DownloadedTrackScreen) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = null,
                    tint = if (isSelected) Color.Blue else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = when (screen) {
                        Screen.TrackScreen -> Icons.Default.Home
                        Screen.PlaybackScreen -> Icons.Default.Home
                        else -> Icons.Default.Home
                    },
                    contentDescription = null,
                    tint = if (isSelected) Color.Blue else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = stringResource(id = screen.label),
                color = if (isSelected) Color.Blue else Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

