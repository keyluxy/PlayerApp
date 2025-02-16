package com.example.playerapp.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playerapp.R
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var playerViewModel: PlayerViewModel

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        startForeground(1, buildNotification()) // Запуск уведомления
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayback()
            ACTION_NEXT -> playerViewModel.nextTrack()
            ACTION_PREV -> playerViewModel.previousTrack()
            else -> playCurrentTrack()
        }
    }

    private fun playCurrentTrack() {
        playerViewModel.currentTrack.value?.let { track ->
            mediaPlayer.reset()
            track.localFilePath?.let { path ->
                mediaPlayer.setDataSource(path)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }
        }
        updateNotification() // Обновляем уведомление при смене трека
    }

    private fun togglePlayback() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause() else mediaPlayer.start()
        updateNotification() // Обновляем уведомление при паузе/воспроизведении
    }

    private fun updateNotification() {
        val notification = buildNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification) // Обновляем уведомление
    }

    private fun getPendingIntent(action: String): PendingIntent {
        return PendingIntent.getService(
            this,
            0,
            Intent(this, MusicService::class.java).setAction(action),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildNotification(): Notification {
        val playPauseIcon = if (mediaPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play

        return NotificationCompat.Builder(this, "music_channel")
            .setContentTitle("Сейчас играет")
            .setContentText(playerViewModel.currentTrack.value?.title ?: "Нет трека")
            .setSmallIcon(R.drawable.ic_default) // Иконка уведомления
            .addAction(R.drawable.ic_previous, "Previous", getPendingIntent(ACTION_PREV))
            .addAction(playPauseIcon, "Play/Pause", getPendingIntent(ACTION_PLAY_PAUSE))
            .addAction(R.drawable.ic_next, "Next", getPendingIntent(ACTION_NEXT))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Видимость на заблокированном экране
            .setPriority(NotificationCompat.PRIORITY_LOW) // Приоритет уведомления
            .build()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "action_play_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREV = "action_prev"
    }
}