package com.example.playerapp.presentation.viewmodel.state

import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.models.Track

data class DownloadedTrackState(
    val isLoading: Boolean = false,
    val tracks: List<DownloadedTrackEntity> = emptyList(),
    val error: String? = null
)


