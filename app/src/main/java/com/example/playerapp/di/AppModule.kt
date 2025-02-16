package com.example.playerapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.playerapp.data.api.DeezerApiService
import com.example.playerapp.data.database.AppDatabase
import com.example.playerapp.data.database.DownloadedTrackDao
import com.example.playerapp.data.repository.TrackRepository
import com.example.playerapp.presentation.viewmodel.PlayerViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePlayerViewModel(trackRepository: TrackRepository): PlayerViewModel {
        return PlayerViewModel(trackRepository)
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "playerapp_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideDownloadedTrackDao(database: AppDatabase): DownloadedTrackDao {
        return database.downloadedTrackDao()
    }

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }
}

