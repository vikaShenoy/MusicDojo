package com.example.musicdojo.model

import androidx.room.*

/**
 * Stores the results of games the user has played.
 */
@Entity(tableName = "game_results")
class GameResult(val mode: String, val score: Float, val date: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}