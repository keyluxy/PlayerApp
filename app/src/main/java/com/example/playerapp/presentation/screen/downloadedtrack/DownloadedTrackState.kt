package com.example.playerapp.presentation.screen.downloadedtrack

import com.example.playerapp.data.database.DownloadedTrackEntity


data class DownloadedTrackState(
    val isLoading: Boolean = false,
    val tracks: List<DownloadedTrackEntity> = emptyList(),
    val error: String? = null
)
