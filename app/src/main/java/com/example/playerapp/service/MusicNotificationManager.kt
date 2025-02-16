package com.example.playerapp.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.playerapp.R
import com.example.playerapp.data.models.Track

class MusicNotificationManager(
    private val context: Context,
    private val mediaSession: MediaSessionCompat
) {


    suspend fun createNotification(track: Track, isPlaying: Boolean): Notification {
        val playPauseAction = NotificationCompat.Action(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
            if (isPlaying) "Pause" else "Play",
            getPendingIntent(MusicServiceActions.ACTION_PLAY_PAUSE)
        )

        var largeIcon: Bitmap? = null
        track.album.cover?.let { coverUrl ->
            runCatching {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(coverUrl)
                    .allowHardware(false)
                    .build()
                val drawable = imageLoader.execute(request).drawable
                (drawable as? BitmapDrawable)?.bitmap
            }.getOrNull()?.let { bitmap ->
                largeIcon = Bitmap.createScaledBitmap(bitmap, 128, 128, false)
            }
        }

        return NotificationCompat.Builder(context, "music_channel")
            .setSmallIcon(R.drawable.ic_default)
            .setContentTitle(track.title)
            .setContentText(track.artist.name)
            .setLargeIcon(largeIcon)
            .addAction(
                R.drawable.ic_previous,
                "Previous",
                getPendingIntent(MusicServiceActions.ACTION_PREV)
            )
            .addAction(playPauseAction)
            .addAction(
                R.drawable.ic_next,
                "Next",
                getPendingIntent(MusicServiceActions.ACTION_NEXT)
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, MusicService::class.java).apply { this.action = action }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
