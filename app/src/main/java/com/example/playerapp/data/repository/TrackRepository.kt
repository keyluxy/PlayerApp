package com.example.playerapp.data.repository

import android.content.Context
import com.example.playerapp.data.api.DeezerApiService
import com.example.playerapp.data.database.DownloadedTrackDao
import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val apiService: DeezerApiService,
    private val downloadedTrackDao: DownloadedTrackDao,
    private val context: Context
) {

    suspend fun fetchTopTracks(): Result<List<Track>> {
        return try {
            val response = apiService.getTopTracks()
            if (response.isSuccessful && response.body() != null) {
                val tracks = response.body()!!.tracks.data
                Result.success(tracks)
            } else {
                Result.failure(Exception("Failed to fetch top tracks"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = apiService.searchTracks(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to search tracks: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getAllDownloadedTracks(): Flow<List<DownloadedTrackEntity>> {
        return downloadedTrackDao.getAllDownloadedTracks()
    }

    suspend fun deleteDownloadedTrack(track: DownloadedTrackEntity) {
        File(track.localFilePath).delete() // Удаление файла
        downloadedTrackDao.delete(track)
    }


    suspend fun downloadTrack(track: Track): Boolean {
        return try {
            val fileUrl = track.preview ?: return false
            val file = downloadFile(fileUrl)

            val downloadedTrack = DownloadedTrackEntity(
                id = track.id.toString(),
                title = track.title,
                artist = track.artist.name,
                url = fileUrl,
                localFilePath = file.absolutePath,
                coverUrl = track.album.cover ?: ""
            )
            downloadedTrackDao.insert(downloadedTrack)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun downloadFile(url: String): File {
        val response = apiService.downloadTrack(url)
        if (!response.isSuccessful) {
            throw Exception("Ошибка скачивания: ${response.message()}")
        }

        val file = File(context.filesDir, "track_${System.currentTimeMillis()}.mp3")
        response.body()?.byteStream()?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }
}
