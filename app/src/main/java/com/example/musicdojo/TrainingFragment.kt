package com.example.musicdojo

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicdojo.model.Game
import com.example.musicdojo.model.Mode
import com.example.musicdojo.model.Question
import kotlinx.android.synthetic.main.fragment_training.*

class TrainingFragment : Fragment() {

    private lateinit var ctx: Context

    private lateinit var game: Game

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }
        return inflater.inflate(R.layout.fragment_training, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Interval spinner
        ArrayAdapter.createFromResource(
            ctx,
            R.array.intervals,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intervalSpinner.adapter = adapter
        }

        startBtn.setOnClickListener {
            val testGame = Game("Interval", ctx, Mode.INTERVAL, 5)
            startGame(testGame)
        }

        selectAnswerBtn.setOnClickListener {
            answerSelected()
        }
    }

    private fun answerSelected() {
        game.submitAnswer(INTERVALS[intervalSpinner.selectedItem])

        questionText.text = getString(R.string.question, game.currentQuestionIdx, game.numQuestions)
        if (game.isFinished()) {
            finishGame()
        } else {
            playSounds(game.getCurrentQuestion())
        }
    }

    private fun startGame(newGame: Game) {
        game = newGame
        gameNameText.text = game.name
        questionText.text = getString(R.string.question, game.currentQuestionIdx, game.numQuestions)
        playSounds(game.getCurrentQuestion())
    }

    private fun finishGame() {
        Toast.makeText(ctx, "Score: ${game.score}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Play the two tones sequentially to the user.
     * @param question: contains the ID for the two tones to play.
     */
    private fun playSounds(question: Question) {
        var mediaPlayer: MediaPlayer = MediaPlayer.create(ctx, question.soundOne)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(ctx, question.soundTwo)
            mediaPlayer.start()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrainingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}