package com.example.musicdojo.database

import androidx.room.*
import com.example.musicdojo.model.GameResult

@Dao
interface GameResultDao {
    @Insert
    fun insert(result: GameResult): Long

    @Update
    fun update(result: GameResult)

    @Delete
    fun delete(result: GameResult)

    @Query("SELECT * FROM game_results")
    fun getAll(): MutableList<GameResult>
}
