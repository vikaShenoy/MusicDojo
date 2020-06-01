package com.example.musicdojo.util

import com.example.musicdojo.model.Mode

val INTERVALS = mapOf<String, Int>(
    "Minor 2nd" to 1,
    "Major 2nd" to 2,
    "Minor 3rd" to 3,
    "Major 3rd" to 4,
    "Perfect 4th" to 5,
    "Diminished 5th" to 6,
    "Perfect 5th" to 7,
    "Minor 6th" to 8,
    "Major 6th" to 9,
    "Minor 7th" to 10,
    "Major 7th" to 11,
    "Octave" to 12
)

val MODES = mapOf<String, Mode>(
    "Intervals" to Mode.INTERVAL,
    "Pitches" to Mode.PITCH,
    "Chords" to Mode.CHORD
)
