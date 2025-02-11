package com.example.playerapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String?, // URL на превью трека
    val position: Int
)

@Serializable
data class Artist(
    val id: Long,
    val name: String,
    val picture: String? // URL картинки исполнителя
)

@Serializable
data class Album(
    val id: Long,
    val title: String,
    val cover: String? // URL картинки обложки альбома
)

@Serializable
data class TracksData(
    val data: List<Track> // Список треков
)

@Serializable
data class ChartResponse(
    val tracks: TracksData // Вложенный объект с треками
)
