package com.example.musicdojo

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.musicdojo.model.Game
import com.example.musicdojo.model.Mode
import com.example.musicdojo.model.Question
import kotlinx.android.synthetic.main.fragment_training.*

class TrainingFragment : Fragment() {

    private lateinit var ctx: Context

    private var playing : Boolean = false

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
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            intervalSpinner.adapter = adapter
        }

        startBtn.setOnClickListener{
            val testGame = Game("Interval", 5, Mode.INTERVAL)
            playGame(testGame)
        }
    }

    private fun playGame(game: Game) {
        var currentScore: Int = 0
        gameNameText.text = game.name

        playing = true

        for (n in 1..game.numQuestions) {
            scoreText.text = getString(R.string.score, currentScore, game.numQuestions)
            val question = generateQuestion(game.mode)
            playSounds(question)
        }
    }

    /**
     * Play the two tones sequentially to the user.
     * @param question: contains the ID for the two tones to play.
     */
    private fun playSounds(question: Question) {
        var mediaPlayer : MediaPlayer = MediaPlayer.create(ctx, question.soundOne)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(ctx, R.raw.two)
            mediaPlayer.start()
        }
    }

    /**
     * Randomly select two tones based on the game type.
     * @param mode: Mode of the game (interval, pitch, chords)
     * @return question: Question object with the two tones to be
     * played and the answer to the question.
     */
    private fun generateQuestion(mode: Mode): Question {
        //TODO - implement this based on different modes
        return Question(R.raw.one, R.raw.two, 5)
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