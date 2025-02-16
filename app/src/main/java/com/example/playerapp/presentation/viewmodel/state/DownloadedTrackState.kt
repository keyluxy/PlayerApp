package com.example.playerapp.presentation.viewmodel.state

import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.models.Track

data class DownloadedTrackState(
    val isLoading: Boolean = false,
    val tracks: List<DownloadedTrackEntity> = emptyList(), // Должно быть DownloadedTrackEntity
    val error: String? = null
)


