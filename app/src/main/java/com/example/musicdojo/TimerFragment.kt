package com.example.musicdojo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicdojo.model.GameResult
import kotlinx.android.synthetic.main.fragment_timer.*
import org.w3c.dom.Text
import java.util.*
import java.util.zip.Inflater
import kotlin.concurrent.schedule

// In seconds
private const val DEFAULT_TOTAL_TIME = 300

/**
 * Fragment which allows the user to set and use a countdown timer.
 */
class TimerFragment : Fragment() {

    private lateinit var ctx: Context
    private var totalTime: Int = DEFAULT_TOTAL_TIME
    private var countdownTime: Int = 0
    private var isCountingDown: Boolean = false
    private var timer: Timer? = null

    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countdowntTxt.setOnClickListener {
            if (!isCountingDown) {
                setTime()
            }
            setTime()
        }
        countdownBtn.setOnClickListener {
            if (!isCountingDown) {
                startCountdown()
            } else {
                stopCountdown()
            }
        }
    }

    /**
     * Create and show a dialog for selecting how much time to count down from.
     */
    private fun setTime() {
        val timeDialog = createTimeDialog(totalTime) {
            if (it.chars().allMatch(Character::isDigit)) {
                totalTime = it.toInt()
                countdownTime = totalTime
                updateCountdownText()
            } else {
                Toast.makeText(
                    ctx,
                    resources.getString(R.string.time_format_error), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        timeDialog.show()
    }

    /**
     * Create a dialog popup for the user to select the countdown time.
     * @param time: The current total countdown time. Used to display in the edit text for the user
     * so they know what the time is currently set at.
     * @param onSave: Callback to perform when the user clicks on the save button.
     */
    private fun createTimeDialog(time: Int, onSave: (String) -> Unit): AlertDialog {
        val setTimeView = layoutInflater.inflate(R.layout.set_time, null)
        val saveTimeBtn: Button = setTimeView.findViewById(R.id.saveTimeBtn)
        val timeEditTxt: TextView = setTimeView.findViewById(R.id.timeEditTxt)


        val dialogBuilder = AlertDialog.Builder(ctx)
        dialogBuilder.setView(setTimeView)
        val dialog = dialogBuilder.create()

        timeEditTxt.text = time.toString()
        saveTimeBtn.setOnClickListener {
            onSave(timeEditTxt.text.toString())
            dialog.dismiss()
        }

        return dialog
    }

    /**
     * Start counting down from the total countdown time. Update the textview
     * on a UI thread every second. Stop when 0 seconds is reached.
     */
    private fun startCountdown() {
        countdownBtn.text = resources.getString(R.string.stop)
        isCountingDown = true
        timer = Timer()
        countdownTime = totalTime
        timer?.schedule(0, 1000) {
            countdownTime -= 1
            activity?.runOnUiThread(Runnable { updateCountdownText() })
            if (countdownTime <= 0) {
                activity?.runOnUiThread(Runnable {
                    stopCountdown()
                    playBeep()
                })

            }
        }
    }

    /**
     * Refresh state of button, stop countdown timer object.
     */
    private fun stopCountdown() {
        isCountingDown = false
        timer?.cancel()
        countdownBtn.text = resources.getString(R.string.start)
        releaseTimer()
    }

    /**
     * Play a beep tone.
     */
    private fun playBeep() {
        val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        tone.startTone(ToneGenerator.TONE_PROP_BEEP)
        tone.release()
    }

    /**
     * Update the state of the text view showing remaining time. Used as countdown is happening.
     */
    private fun updateCountdownText() {
        countdowntTxt.text = formatTime()
    }

    /**
     * Convert the time from an integer value of seconds to a format
     * using string templates, ex. 5:11, 5:04.
     */
    private fun formatTime(): String {
        val minutes = countdownTime / 60
        val seconds = countdownTime % 60
        return if (minutes > 0 && seconds == 0) {
            resources.getString(R.string.countdown_min_sec_zero, minutes)
        } else if (minutes > 0 && seconds >= 10) {
            resources.getString(R.string.countdown_min, minutes, seconds)
        } else if (minutes > 0 && seconds < 10) {
            resources.getString(R.string.countdown_sec_under_ten, minutes, seconds)
        } else {
            resources.getString(R.string.countdown_sec, seconds)
        }
    }

    /**
     * Stop counting down with the timer object.
     */
    private fun releaseTimer() {
        timer?.cancel()
        timer = null
    }

    /**
     * Release the timer resources on the stop activity lifecycle method.
     */
    override fun onStop() {
        super.onStop()
        releaseTimer()
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
            TimerFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}