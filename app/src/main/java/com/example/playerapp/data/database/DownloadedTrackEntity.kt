package com.example.playerapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playerapp.data.models.Album
import com.example.playerapp.data.models.Artist
import com.example.playerapp.data.models.Track

@Entity(tableName = "downloaded_tracks")
data class DownloadedTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val url: String,
    val localFilePath: String,
    val coverUrl: String
)

fun DownloadedTrackEntity.toTrack(): Track {
    return Track(
        id = this.id.toLongOrNull() ?: 0L,
        title = this.title,
        artist = Artist(
            id = 0,
            name = this.artist,
            picture = this.coverUrl
        ),
        album = Album(
            id = 0,
            title = this.title,
            cover = this.coverUrl
        ),
        preview = this.url,
        position = 0,
        localFilePath = this.localFilePath
    )
}