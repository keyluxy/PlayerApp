package com.example.playerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.models.Track
import com.example.playerapp.data.repository.TrackRepository
import com.example.playerapp.presentation.viewmodel.intent.TrackIntent
import com.example.playerapp.presentation.viewmodel.state.TrackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    private val _state = MutableStateFlow(TrackState())
    val state: StateFlow<TrackState> = _state

    private val intentChannel = Channel<TrackIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    fun sendIntent(intent: TrackIntent) {
        viewModelScope.launch { intentChannel.send(intent) }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            for (intent in intentChannel) {
                when (intent) {
                    is TrackIntent.SearchTracks -> searchTracks(intent.query)
                    TrackIntent.FetchTopTracks -> fetchTopTracks()
                    is TrackIntent.DownloadTrack -> downloadTrack(intent.track)
                }
            }
        }
    }

    private fun fetchTopTracks() {
        updateState(TrackState(isLoading = true))
        viewModelScope.launch {
            trackRepository.fetchTopTracks().fold(
                onSuccess = { tracks ->
                    updateState(TrackState(tracks = tracks))
                },
                onFailure = { error ->
                    updateState(TrackState(error = error.message))
                }
            )
        }
    }

    private fun searchTracks(query: String) {
        updateState(TrackState(isLoading = true))
        viewModelScope.launch {
            trackRepository.searchTracks(query).fold(
                onSuccess = { tracks ->
                    updateState(TrackState(tracks = tracks))
                },
                onFailure = { error ->
                    updateState(TrackState(error = error.message))
                }
            )
        }
    }

    private fun downloadTrack(track: Track) {
        viewModelScope.launch {
            trackRepository.downloadTrack(track).fold(
                onSuccess = { success ->
                    if (success) {
                        fetchTopTracks()
                    }
                },
                onFailure = { error ->
                    updateState(TrackState(error = error.message))
                }
            )
        }
    }


    private fun updateState(newState: TrackState) {
        _state.value = newState
    }
}