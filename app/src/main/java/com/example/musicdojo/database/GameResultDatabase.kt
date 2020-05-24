package com.example.musicdojo.database

import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.musicdojo.model.GameResult

@Database(entities = [GameResult::class], version = 1)
abstract class GameResultDatabase: RoomDatabase() {

    abstract fun gameResultDao(): GameResultDao

}