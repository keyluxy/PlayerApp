package com.example.playerapp.data.api

import android.content.Context
import com.example.playerapp.data.database.DownloadedTrackDao
import com.example.playerapp.data.repository.TrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "https://api.deezer.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDeezerApiService(retrofit: Retrofit): DeezerApiService {
        return retrofit.create(DeezerApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTrackRepository(
        apiService: DeezerApiService,
        downloadedTrackDao: DownloadedTrackDao,
        context: Context
    ): TrackRepository {
        return TrackRepository(apiService, downloadedTrackDao, context)
    }
}
