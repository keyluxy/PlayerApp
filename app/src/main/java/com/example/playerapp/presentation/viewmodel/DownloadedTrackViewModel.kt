package com.example.playerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.database.toTrack
import com.example.playerapp.data.repository.TrackRepository
import com.example.playerapp.presentation.viewmodel.intent.DownloadedTrackIntent
import com.example.playerapp.presentation.viewmodel.state.DownloadedTrackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadedTrackViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val playerViewModel: PlayerViewModel
) : ViewModel() {

    // State flow for managing UI state
    private val _state = MutableStateFlow(DownloadedTrackState())
    val state: StateFlow<DownloadedTrackState> = _state

    // Channel for handling intents
    private val intentChannel = Channel<DownloadedTrackIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    // Function to send intents to the ViewModel
    fun sendIntent(intent: DownloadedTrackIntent) {
        viewModelScope.launch { intentChannel.send(intent) }
    }

    // Function to handle intents
    private fun handleIntents() {
        viewModelScope.launch {
            for (intent in intentChannel) {
                when (intent) {
                    DownloadedTrackIntent.RefreshDownloads -> refreshDownloads()
                    is DownloadedTrackIntent.DeleteTrack -> deleteTrack(intent.track)
                }
            }
        }
    }

    private fun deleteTrack(track: DownloadedTrackEntity) {
        viewModelScope.launch {
            trackRepository.deleteDownloadedTrack(track)
            playerViewModel.notifyTracksUpdated()
            refreshDownloads()
        }
    }


    // Function to refresh downloaded tracks
    private fun refreshDownloads() {
        viewModelScope.launch {
            trackRepository.getAllDownloadedTracks().collect { tracks ->
                _state.value = DownloadedTrackState(tracks = tracks) // Убираем преобразование
            }
        }
    }



    // Function to update the state (if needed)
    private fun updateState(newState: DownloadedTrackState) {
        _state.value = newState
    }
}