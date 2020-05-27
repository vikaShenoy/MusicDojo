package com.example.musicdojo

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.musicdojo.database.GameResultAdapter
import com.example.musicdojo.model.Game
import com.example.musicdojo.model.Question
import com.example.musicdojo.model.GameResult
import com.example.musicdojo.util.INTERVALS
import com.example.musicdojo.util.MODES
import com.example.musicdojo.util.MODE_DEFAULT
import com.example.musicdojo.util.NUM_QUESTIONS_DEFAULT
import kotlinx.android.synthetic.main.fragment_training.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class TrainingFragment : Fragment(), SensorEventListener {

    private lateinit var ctx: Context
    private lateinit var game: Game
    private lateinit var mSensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gameResultAdapter: GameResultAdapter

    private val SHAKE_TIMEOUT = 500
    private val SHAKE_THRESHOLD = 4.0f
    private val NUM_SHAKES = 3
    private var shakeCount = 0
    private var lastShakeTime: Long = 0

    private var gameActive = false
    private var player: MediaPlayer? = null

    private lateinit var prefs: SharedPreferences

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
            mSensorManager.registerListener(
                this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        gameResultAdapter = GameResultAdapter(ctx)
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
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
            val modeName = prefs.getString(
                "selected_mode",
                MODE_DEFAULT
            )
            val n: Int? = prefs.getString(
                "num_questions",
                NUM_QUESTIONS_DEFAULT
            )?.toInt()
            val mode = MODES[modeName]
            if (mode != null && modeName != null && n != null) {
                startGame(Game(modeName, ctx, mode, n))
            }
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
        intervalSpinner.visibility = View.INVISIBLE


    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }

    /**
     * Release the media player if the user exits the activity.
     */
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }


    /**
     * Vibrate the device.
     * @param length: How many milliseconds the vibration should last
     */
    private fun vibrate(length: Long) {
        val vibrator =
            ctx.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                length, VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    /**
     * Callback for when the user locks in an answer to the current question.
     * Progresses to the next question or finishes the game if there are no more.
     */
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

    /**
     * Initialise a new game.
     * Change the state to game active and sound the first question.
     * @param newGame: game object which is now active.
     */
    private fun startGame(newGame: Game) {
        game = newGame
        gameActive = true
        flipButtons()

        gameNameText.text = game.name
        questionText.text =
            getString(R.string.question, game.currentQuestionIdx + 1, game.numQuestions)
        intervalSpinner.setSelection(0)
        playSounds(game.getCurrentQuestion())
    }

    /**
     * Called when the last question of the game in answered.
     * Open a score dialog and provide func to save the result to
     * Room DB if the user wants.
     */
    private fun finishGame() {
        gameActive = false
        flipButtons()

        val scoreDialog = createScoreDialog(game) {
            val result: GameResult = GameResult(
                game.mode.toString(),
                (game.score / game.numQuestions).toFloat(),
                getDate("dd/MM/yyyy")
            )
            gameResultAdapter.insert(gameResult = result)
        }
        scoreDialog.show()
    }

    /**
     * Return the current date.
     * @param pattern: String pattern for the date to be formatted in.
     * @return formatted current date string.
     */
    private fun getDate(pattern: String): String {
        val currentDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat(pattern)
        return formatter.format(currentDate)
    }


    /**
     * Create a dialog for displaying the user's game result.
     * @param game: Game the user just played. Has score as a property.
     * @param onSave: callback to be done when the user clicks the save score button.
     * @return an AlertDialog popup.
     */
    private fun createScoreDialog(game: Game, onSave: () -> Unit): android.app.AlertDialog {
        val scoreView = layoutInflater.inflate(R.layout.save_score, null)

        val gameNameTxt = scoreView.findViewById<TextView>(R.id.saveGameNameTxt)
        val scoreTxt = scoreView.findViewById<TextView>(R.id.saveScoreTxt)
        val saveBtn = scoreView.findViewById<Button>(R.id.saveScoreBtn)
        val sendBtn = scoreView.findViewById<Button>(R.id.sendBtn)

        gameNameTxt.text = game.name
        scoreTxt.text = resources.getString(R.string.score, game.score, game.numQuestions)
        saveBtn.setOnClickListener {
            onSave()
        }

        sendBtn.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                resources.getString(R.string.email_subject)
            )
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                resources.getString(R.string.email_body, game.score, game.numQuestions, game.name)
            )
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }

        val dialogBuilder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(ctx)
        dialogBuilder.setView(scoreView)
        return dialogBuilder.create()
    }

    /**
     * Toggle the widgets which are visible based on whether the user is playing
     * a game currently or not.
     */
    private fun flipButtons() {
        if (gameActive) {
            questionText.visibility = View.VISIBLE
            gameNameText.visibility = View.VISIBLE
            replayBtn.visibility = View.VISIBLE
            selectAnswerBtn.visibility = View.VISIBLE
            intervalSpinner.visibility = View.VISIBLE
            startBtn.visibility = View.INVISIBLE
        } else {
            questionText.visibility = View.INVISIBLE
            gameNameText.visibility = View.INVISIBLE
            replayBtn.visibility = View.INVISIBLE
            selectAnswerBtn.visibility = View.INVISIBLE
            intervalSpinner.visibility = View.INVISIBLE
            startBtn.visibility = View.VISIBLE
        }
    }

    /**
     * Play the two tones sequentially to the user.
     * @param question: contains the ID for the two tones to play.
     */
    private fun playSounds(question: Question) {
        releasePlayer()
        player = MediaPlayer.create(ctx, question.soundOne)
        player?.start()
        player?.setOnCompletionListener {
            releasePlayer()
            player = MediaPlayer.create(ctx, question.soundTwo)
            player?.start()
            player?.setOnCompletionListener {
                releasePlayer()
            }
        }
    }

    /**
     * Stop and release the active media player.
     */
    private fun releasePlayer() {
        player?.stop()
        player?.release()
        player = null
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
                    val acl = sqrt(
                        (x * x) + (y * y) + (z * z)) - SensorManager.GRAVITY_EARTH
                    if (acl > SHAKE_THRESHOLD && gameActive) {
                        lastShakeTime = currentShakeTime
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