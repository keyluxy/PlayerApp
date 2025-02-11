package com.example.playerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.models.Track
import com.example.playerapp.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _uiState

    init {
        fetchTopTracks()
    }

    fun fetchTopTracks() {
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
}

data class TrackUiState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)