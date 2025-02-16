package com.example.playerapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_tracks")
data class DownloadedTrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val url: String,
    val localFilePath: String,
    val coverUrl: String
)

