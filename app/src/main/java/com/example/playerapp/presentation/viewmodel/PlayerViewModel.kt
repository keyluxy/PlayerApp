package com.example.playerapp.presentation.viewmodel


import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.models.Album
import com.example.playerapp.data.models.Artist
import com.example.playerapp.data.models.Track
import com.example.playerapp.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
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
    private val _trackList = MutableStateFlow<List<Track>>(emptyList())
    private var currentTrackIndex = -1
    var trackDuration = 0
    private val _updateTracksEvent = MutableSharedFlow<Unit>()
    private var progressJob: Job? = null
    private var isProgressUpdatesActive = false

    init {
        loadTracks()
        observeDownloadedTracks()
    }


    private fun observeDownloadedTracks() {
        viewModelScope.launch {
            trackRepository.getAllDownloadedTracks()
                .collect { downloadedTracks ->
                    _trackList.value = downloadedTracks.map { it.toTrack() }
                    if (currentTrackIndex >= _trackList.value.size) {
                        currentTrackIndex = -1
                    }
                }
        }
    }

    // Преобразование DownloadedTrackEntity в Track
    private fun DownloadedTrackEntity.toTrack(): Track {
        return Track(
            id = this.id.toLong(),
            title = this.title,
            artist = Artist(0, this.artist, null),
            album = Album(0, this.title, this.coverUrl),
            preview = this.url,
            position = 0,
            localFilePath = this.localFilePath
        )
    }

    private fun loadTracks() {
        viewModelScope.launch {
            trackRepository.fetchTopTracks().onSuccess { tracks ->
                _trackList.value = tracks
            }
        }
    }


    fun setLocalTracks(tracks: List<Track>) {
        _trackList.value = tracks
            .filter { track ->
                track.localFilePath?.let { File(it).exists() } ?: true
            }
        currentTrackIndex = tracks.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentTrackIndex == -1 && tracks.isNotEmpty()) {
            currentTrackIndex = 0
        }
    }

    fun playTrack(track: Track) {
        val index = _trackList.value.indexOfFirst { it.id == track.id }
        if (index != -1) {
            currentTrackIndex = index
        }

        if (track.localFilePath != null && !File(track.localFilePath).exists()) {
            _currentTrack.value = null
            _isPlaying.value = false
            return
        }

        _currentTrack.value = track
        val dataSource = track.localFilePath ?: track.preview

        dataSource?.let { source ->
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(source)
                setOnPreparedListener {
                    trackDuration = it.duration
                    start()
                    _isPlaying.value = true
                    isProgressUpdatesActive = true
                    startProgressUpdates()
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    isProgressUpdatesActive = false
                    nextTrack()
                }
                if (track.localFilePath != null) prepare() else prepareAsync()
            }
        }
    }

    private fun startProgressUpdates() {
        progressJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val currentPos = player.currentPosition
                        _progress.value = currentPos
                        Log.d("Progress", "Current: $currentPos, Total: $trackDuration")
                    }
                }
            }
        }
    }


    fun notifyTracksUpdated() {
        viewModelScope.launch {
            _updateTracksEvent.emit(Unit)
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
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun nextTrack() {
        val tracks = _trackList.value
        if (tracks.isEmpty()) return

        currentTrackIndex = (currentTrackIndex + 1).mod(tracks.size)
        playTrack(tracks[currentTrackIndex])
    }

    fun previousTrack() {
        val tracks = _trackList.value
        if (tracks.isEmpty()) return

        currentTrackIndex = (currentTrackIndex - 1).mod(tracks.size)
        playTrack(tracks[currentTrackIndex])
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        isProgressUpdatesActive = false
    }

}

