package com.example.playerapp.data.repository

import android.content.Context
import com.example.playerapp.data.api.DeezerApiService
import com.example.playerapp.data.database.DownloadedTrackDao
import com.example.playerapp.data.database.DownloadedTrackEntity
import com.example.playerapp.data.models.Track
import kotlinx.coroutines.flow.Flow
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

    fun getAllDownloadedTracks(): Flow<List<DownloadedTrackEntity>> {
        return downloadedTrackDao.getAllDownloadedTracks()
    }

    suspend fun deleteDownloadedTrack(track: DownloadedTrackEntity) {
        val file = File(track.localFilePath)
        if (file.exists() && file.delete()) {
            downloadedTrackDao.delete(track)
        } else {
            throw Exception("Failed to delete file at ${track.localFilePath}")
        }
    }

    suspend fun downloadTrack(track: Track): Result<Boolean> {
        return try {
            val fileUrl = track.preview ?: return Result.failure(Exception("Track preview URL is null"))
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
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun downloadFile(url: String): File {
        val response = apiService.downloadTrack(url)
        if (!response.isSuccessful) {
            throw Exception("Ошибка скачивания: ${response.message()}")
        }

        val filesDir = context.filesDir
        if (!filesDir.exists()) {
            filesDir.mkdirs() // Ensure the directory exists
        }

        val file = File(filesDir, "track_${System.currentTimeMillis()}.mp3")
        response.body()?.byteStream()?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }
}