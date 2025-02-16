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

    private val _state = MutableStateFlow(DownloadedTrackState())
    val state: StateFlow<DownloadedTrackState> = _state

    private val intentChannel = Channel<DownloadedTrackIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    fun sendIntent(intent: DownloadedTrackIntent) {
        viewModelScope.launch { intentChannel.send(intent) }
    }

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


    private fun refreshDownloads() {
        viewModelScope.launch {
            trackRepository.getAllDownloadedTracks().collect { tracks ->
                _state.value = DownloadedTrackState(tracks = tracks)
            }
        }
    }

}