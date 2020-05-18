package com.example.musicdojo.model

class Exercise(
    val name: String = "",
    val description: String = "",
    val category: Category? = null,
    val bpm: Int = 0,
    val targetBpm: Int = 0,
    val duration: Int = 0
) {
}