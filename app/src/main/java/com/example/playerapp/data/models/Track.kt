@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.example.playerapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String?,
    val position: Int
)

@Serializable
data class Artist(
    val id: Long,
    val name: String,
    val picture: String?
)

@Serializable
data class Album(
    val id: Long,
    val title: String,
    val cover: String?
)

@Serializable
data class TracksData(
    val data: List<Track>
)

