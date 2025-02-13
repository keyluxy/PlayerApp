package com.example.playerapp.presentation.viewmodel


import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.models.Track
import com.example.playerapp.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val _progress = MutableStateFlow(0)
    private val _isPlaying = MutableStateFlow(false)
    private val _currentTrack = MutableStateFlow<Track?>(null)

    val progress: StateFlow<Int> = _progress
    val isPlaying: StateFlow<Boolean> = _isPlaying
    val currentTrack: StateFlow<Track?> = _currentTrack
    private var isResumed = false

    private val _trackList = MutableStateFlow<List<Track>>(emptyList())
    private var currentTrackIndex = 0
    var trackDuration = 0

    init {
        loadTracks()

        viewModelScope.launch {
            while (true) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        _progress.value = it.currentPosition
                    }
                }
                delay(1000)
            }
        }
    }


    private fun loadTracks() {
        viewModelScope.launch {
            trackRepository.fetchTopTracks().onSuccess { tracks ->
                _trackList.value = tracks
                if (tracks.isNotEmpty()) {
                    _currentTrack.value = tracks.first()
                }
            }
        }
    }


    fun playTrack(url: String, track: Track) {
        _currentTrack.value = track
        if (!isResumed) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    trackDuration = duration
                    _isPlaying.value = true
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    nextTrack()
                }
            }
        } else {
            mediaPlayer?.start()
        }
    }


    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _progress.value = position
    }


    @SuppressLint("DefaultLocale")
    fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun nextTrack() {
        val tracks = _trackList.value
        if (tracks.isNotEmpty()) {
            currentTrackIndex = (currentTrackIndex + 1) % tracks.size
            playTrack(tracks[currentTrackIndex].preview ?: "", tracks[currentTrackIndex])
        }
    }

    fun previousTrack() {
        val tracks = _trackList.value
        if (tracks.isNotEmpty()) {
            currentTrackIndex = if (currentTrackIndex - 1 < 0) tracks.size - 1 else currentTrackIndex - 1
            playTrack(tracks[currentTrackIndex].preview ?: "", tracks[currentTrackIndex])
        }
    }

    override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }


}

