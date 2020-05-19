package com.example.musicdojo.model

import androidx.room.*

@Entity(tableName = "schedules")
class Schedule(
    val name: String = "",
    val exercises: List<Exercise>? = null,
    val completed: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}

@Dao
interface ScheduleDao {
    @Insert
    fun insert(schedule: Schedule): Long

    @Update
    fun update(schedule: Schedule)

    @Query("SELECT * FROM schedules")
    fun getAll(): List<Schedule>

    @Delete
    fun delete(schedule: Schedule)
}