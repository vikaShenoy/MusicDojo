package com.example.musicdojo.model

import android.content.Context
import com.example.musicdojo.R
import java.util.*
import kotlin.collections.ArrayList

class Game(
    val name: String,
    val ctx: Context,
    val mode: Mode = Mode.INTERVAL,
    val numQuestions: Int
) {
    val questions: List<Question> = generateQuestions()
    var currentQuestionIdx: Int = 0
    var score: Int = 0

    // TODO - support other modes
    /**
     * Create the game's questions with random selection of pitches.
     * @param mode: game mode. Will be used to select chords in chord mode.
     * @param numQuestions: how many questions to generate
     * @return list of questions.
     */
    private fun generateQuestions(): List<Question> {
        val questions: MutableList<Question> = ArrayList<Question>()

        for (i in 0..numQuestions) {
            if (mode == Mode.INTERVAL) {
                questions.add(createQuestion())
            }
        }
        return questions
    }

    /**
     * Generate a single question with a number generator.
     * The range of 12 is used as this is the length of one musical octave.
     * @return a Question object.
     */
    private fun createQuestion() : Question {
        val random = Random()
        val intervalOne = random.nextInt(1..12)
        val intervalTwo = intervalOne + random.nextInt(1..12)
        val firstTone = ctx.resources.getIdentifier(
            "p$intervalOne", "raw", ctx.packageName)
        val secondTone = ctx.resources.getIdentifier(
            "p$intervalTwo", "raw", ctx.packageName)
        return Question(firstTone, secondTone, secondTone - firstTone)
    }

    /**
     * Implementation of number generation to take a range
     * instead of a bound.
     */
    private fun Random.nextInt(range: IntRange): Int {
        return range.first + nextInt(range.last - range.first)
    }

    fun submitAnswer(answer: Int?) {
        if (questions[currentQuestionIdx].answer == answer) {
            score += 1
        }
        currentQuestionIdx += 1
    }

    fun isFinished() : Boolean {
        if (currentQuestionIdx >= numQuestions) {
            return true
        }
        return false
    }

    fun getCurrentQuestion() : Question {
        return questions[currentQuestionIdx]
    }
}