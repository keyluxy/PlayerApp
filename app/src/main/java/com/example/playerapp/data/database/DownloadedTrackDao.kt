package com.example.playerapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: DownloadedTrackEntity)

    @Query("SELECT * FROM downloaded_tracks")
    fun getAllDownloadedTracks(): Flow<List<DownloadedTrackEntity>>

    @Delete
    suspend fun delete(track: DownloadedTrackEntity)

}