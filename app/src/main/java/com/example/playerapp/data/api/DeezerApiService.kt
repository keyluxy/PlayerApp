package com.example.playerapp.data.api

import com.example.playerapp.data.models.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface DeezerApiService {

    @GET("chart")
    suspend fun getTopTracks(): Response<ChartResponse>

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): Response<SearchResponse>

    @GET
    suspend fun downloadTrack(@Url url: String): Response<ResponseBody>
}

@Serializable
data class ChartResponse(
    @SerialName("tracks") val tracks: TracksData
)

@Serializable
data class SearchResponse(
    val data: List<Track>
)

@Serializable
data class TracksData(
    val data: List<Track>
)
