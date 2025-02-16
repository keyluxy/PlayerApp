package com.example.playerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.repository.TrackRepository
import com.example.playerapp.presentation.screen.downloadedtrack.DownloadedTrackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadedTrackViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val playerViewModel: PlayerViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadedTrackState())
    val uiState: StateFlow<DownloadedTrackState> = _uiState

    init {
        viewModelScope.launch {
            trackRepository.getAllDownloadedTracks()
                .collectLatest { tracks ->
                    _uiState.value = DownloadedTrackState(tracks = tracks)
                }
        }
    }

    fun refreshDownloads() {
        viewModelScope.launch {
            trackRepository.getAllDownloadedTracks()
                .collect { tracks ->
                    _uiState.value = DownloadedTrackState(tracks = tracks)
                }
        }
    }

    fun deleteTrack(track: DownloadedTrackEntity) {
        viewModelScope.launch {
            trackRepository.deleteDownloadedTrack(track)
            playerViewModel.notifyTracksUpdated()
        }
    }
}


