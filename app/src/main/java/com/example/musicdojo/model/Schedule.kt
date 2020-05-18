package com.example.musicdojo.model

class Schedule(
    val name: String = "",
    val exercises: List<Exercise>? = null,
    val completed: Boolean = false
) {
}