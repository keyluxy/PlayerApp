package com.example.playerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.models.Track
import com.example.playerapp.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _uiState
    private val _downloadCompleted = MutableSharedFlow<Unit>()
    val downloadCompleted: SharedFlow<Unit> = _downloadCompleted

    init {
        fetchTopTracks()
    }

    private fun fetchTopTracks() {
        viewModelScope.launch {
            _uiState.value = TrackUiState(isLoading = true)
            trackRepository.fetchTopTracks().fold(
                onSuccess = { tracks ->
                    _uiState.value = TrackUiState(tracks = tracks)
                },
                onFailure = { error ->
                    _uiState.value = TrackUiState(error = error.message)
                }
            )
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _uiState.value = TrackUiState(isLoading = true)
            trackRepository.searchTracks(query).fold(
                onSuccess = { tracks ->
                    _uiState.value = TrackUiState(tracks = tracks)
                },
                onFailure = { error ->
                    _uiState.value = TrackUiState(error = error.message)
                }
            )
        }
    }

    fun downloadTrack(track: Track) {
        viewModelScope.launch {
            if (trackRepository.downloadTrack(track)) {
                _downloadCompleted.emit(Unit)
            }
        }
    }
}

data class TrackUiState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)