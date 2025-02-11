package com.example.playerapp.data.repository

import com.example.playerapp.data.api.DeezerApiService
import com.example.playerapp.data.models.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val apiService: DeezerApiService
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
}
