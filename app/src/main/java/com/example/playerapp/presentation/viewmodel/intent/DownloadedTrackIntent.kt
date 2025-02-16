package com.example.playerapp.presentation.viewmodel.intent

import com.example.playerapp.data.database.DownloadedTrackEntity

sealed class DownloadedTrackIntent {
    data object RefreshDownloads : DownloadedTrackIntent()
    data class DeleteTrack(val track: DownloadedTrackEntity) : DownloadedTrackIntent()  // Ожидаем DownloadedTrackEntity
}
