package com.example.playerapp.presentation.viewmodel.state

import com.example.playerapp.data.models.Track

data class TrackState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)