package com.example.musicdojo.model

import androidx.annotation.RequiresPermission
import androidx.room.*

@Entity(tableName = "exercises")
class Exercise(
    val name: String = "",
    val description: String = "",
    val category: Category? = null,
    val bpm: Int = 0,
    @ColumnInfo(name = "target_bpm") val targetBpm: Int = 0,
    val duration: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: Exercise): Long

    @Update
    fun update(exercise: Exercise)

    @Query("SELECT * FROM exercises")
    fun getAll(): List<Exercise>

    @Delete
    fun delete(exercise: Exercise)
}