package com.example.playerapp.presentation.viewmodel.intent

import com.example.playerapp.data.models.Track

sealed class TrackIntent {
    data class SearchTracks(val query: String) : TrackIntent()
    object FetchTopTracks : TrackIntent()
    data class DownloadTrack(val track: Track) : TrackIntent()
}