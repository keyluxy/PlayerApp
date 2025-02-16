package com.example.playerapp.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.playerapp.MainActivity
import com.example.playerapp.R
import com.example.playerapp.data.models.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicService : LifecycleService() {

    private val binder = MusicBinder()
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: MusicNotificationManager
    private var mediaPlayer: MediaPlayer? = null

    // Храним текущий трек
    private var currentTrack: Track? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicService")
        notificationManager = MusicNotificationManager(this, mediaSession)

        // Запускаем foreground service с пустым уведомлением
        startForegroundService()
    }

    // Если передали null, используем сохранённый трек
    fun updateNotification(track: Track?, isPlaying: Boolean) {
        lifecycleScope.launch {
            val trackToShow = track ?: currentTrack
            if (trackToShow != null) {
                notificationManager.showNotification(trackToShow, isPlaying)
            }
        }
    }

    fun playTrack(track: Track) {
        currentTrack = track
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.preview)
            prepareAsync()
            setOnPreparedListener {
                start()
                updateNotification(track, true)
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                mediaSession.controller.transportControls.pause()
                updateNotification(null, isPlaying = false)
            } else {
                it.start()
                mediaSession.controller.transportControls.play()
                updateNotification(null, isPlaying = true)
            }
        }
    }

    // MusicService.kt
    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        lifecycleScope.launch {
            val notification = currentTrack?.let {
                notificationManager.createNotification(it, isPlaying = false)
            } ?: createDefaultNotification()
            startForeground(1, notification)
        }
    }

    private fun createDefaultNotification(): Notification {
        // Создаем PendingIntent для перехода в MainActivity или любое другое Activity
        val defaultIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, "music_channel")
            .setSmallIcon(R.drawable.ic_default)
            .setContentTitle("Music Player")
            .setContentText("Paused")
            .setContentIntent(defaultIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

}

