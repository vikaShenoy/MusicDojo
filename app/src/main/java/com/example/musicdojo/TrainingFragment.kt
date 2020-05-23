package com.example.musicdojo

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.musicdojo.model.Game
import com.example.musicdojo.model.Mode
import com.example.musicdojo.model.Question
import kotlinx.android.synthetic.main.fragment_training.*
import java.lang.Math.sqrt
import kotlin.math.sqrt

class TrainingFragment : Fragment(), SensorEventListener {

    private lateinit var ctx: Context
    private lateinit var game: Game

    private var gameActive = false

    private lateinit var mSensorManager: SensorManager

    private lateinit var accelerometer: Sensor
    private val SHAKE_TIMEOUT = 500
    private val SHAKE_THRESHOLD = 4.0f
    private val NUM_SHAKES = 3
    private var shakeCount = 0

    private var lastShakeTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }
        mSensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
            accelerometer = it
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
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
            vibrate(500)

            val testGame = Game("Intervals", ctx, Mode.INTERVAL, 5)
            startGame(testGame)
        }

        selectAnswerBtn.setOnClickListener {
            val animationSet: AnimatorSet =
                AnimatorInflater.loadAnimator(
                    ctx,
                    R.animator.bounce
                ) as AnimatorSet
            animationSet.setTarget(it)
            animationSet.start()
            answerSelected()
        }

        replayBtn.setOnClickListener {
            if (gameActive) {
                playSounds(game.getCurrentQuestion())
            }
        }

        replayBtn.visibility = View.INVISIBLE
        selectAnswerBtn.visibility = View.INVISIBLE
    }

    private fun vibrate(length: Long) {
        val vibrator =
            ctx.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                length, VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    private fun answerSelected() {
        if (gameActive) {
            game.submitAnswer(INTERVALS[intervalSpinner.selectedItem])
            intervalSpinner.setSelection(0)

            if (game.isFinished()) {
                finishGame()
            } else {
                questionText.text =
                    getString(
                        R.string.question,
                        game.currentQuestionIdx + 1,
                        game.numQuestions
                    )
                playSounds(game.getCurrentQuestion())
            }
        }
    }

    private fun startGame(newGame: Game) {
        game = newGame
        gameActive = true
        gameNameText.text = game.name
        questionText.text =
            getString(R.string.question, game.currentQuestionIdx + 1, game.numQuestions)
        replayBtn.visibility = View.VISIBLE
        startBtn.visibility = View.INVISIBLE
        selectAnswerBtn.visibility = View.VISIBLE
        intervalSpinner.setSelection(0)
        playSounds(game.getCurrentQuestion())
    }

    private fun finishGame() {
        gameActive = false
        selectAnswerBtn.visibility = View.INVISIBLE
        startBtn.visibility = View.VISIBLE
        replayBtn.visibility = View.INVISIBLE
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
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val currentShakeTime = System.currentTimeMillis()

                if (currentShakeTime - lastShakeTime > SHAKE_TIMEOUT) {
                    val diffTime = currentShakeTime - lastShakeTime
                    val accel = sqrt((x * x) + (y * y) + (z * z)) - SensorManager.GRAVITY_EARTH
                    if (accel > SHAKE_THRESHOLD && gameActive) {
                        lastShakeTime = currentShakeTime
                        Log.i("test", "zoo")
                        shakeCount += 1
                        if (shakeCount >= NUM_SHAKES) {
                            playSounds(game.getCurrentQuestion())
                            shakeCount = 0
                        }
                    }
                }

            }
        }
    }
}