package com.example.playerapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import com.example.playerapp.presentation.screen.MainScreen
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import com.example.playerapp.ui.theme.PlayerAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        playerViewModel.startService()
        enableEdgeToEdge()
        setContent {
            PlayerAppTheme {
                MainScreen(playerViewModel = playerViewModel)
            }
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel", // ID канала
                "Music Player", // Название канала
                NotificationManager.IMPORTANCE_LOW // Важность
            ).apply {
                setSound(null, null) // Отключаем звук уведомлений
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

