package com.example.playerapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [DownloadedTrackEntity::class],
    version = 2,
    exportSchema = false
)abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadedTrackDao(): DownloadedTrackDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE downloaded_tracks ADD COLUMN coverUrl TEXT NOT NULL DEFAULT ''"
                )
            }
        }
    }
}
