package com.example.playerapp.di

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
    fun providePlayerViewModel(): PlayerViewModel {
        return PlayerViewModel()
    }
}
