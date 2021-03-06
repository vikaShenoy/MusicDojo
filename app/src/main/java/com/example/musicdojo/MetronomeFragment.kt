package com.example.musicdojo

import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_metronome.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.sqrt

private const val MIN_BPM = 50
private const val MAX_BPM = 250
private const val INITIAL_BPM = 100
private const val TIMEOUT = 1000

private val TEMPO_BUTTONS: List<Int> = arrayListOf(-5, -2, 2, 5)

class MetronomeFragment : Fragment() {

    private lateinit var ctx: Context
    private lateinit var rootView: View
    private var timer: Timer? = null
    private var bpm: Int = INITIAL_BPM
    private var isActive = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }

        rootView = inflater.inflate(R.layout.fragment_metronome, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bpmTxt.text = bpm.toString()
        initSeekBar()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            initTempoButtons()
        }
        startStopBtn.setOnClickListener {
            toggleMetronome()
        }
    }

    /**
     * Add the correct label and listener to each of the four tempo buttons.
     * Values for the buttons are specified in a const array.
     */
    private fun initTempoButtons() {
        initTempoButton(btn1, TEMPO_BUTTONS[0])
        initTempoButton(btn2, TEMPO_BUTTONS[1])
        initTempoButton(btn3, TEMPO_BUTTONS[2])
        initTempoButton(btn4, TEMPO_BUTTONS[3])
    }

    /**
     * Add a label to each button representing how it increases/decreases bpm.
     * Add a listener to change bpm based on these values.
     * @param button: Button to init.
     * @param value: How much to add or remove to the bpm of the metronome on click.
     */
    private fun initTempoButton(btn: Button, value: Int) {
        if (value.toString()[0] == '-') {
            btn.text = value.toString()
        } else {
            btn.text = resources.getString(R.string.btnInc, value)
        }
        btn.setOnClickListener {
            updateBpm(bpm + value)
        }
    }

    /**
     * Change the speed of the metronome. Restart the metronome at this bpm if
     * it is currently playing.
     * @param tempo: The new tempo to play the metronome at.
     */
    private fun updateBpm(tempo: Int) {
        bpm = tempo
        if (bpmTxt != null) {
            bpmTxt.text = tempo.toString()
        }

        if (metronomeBar != null) {
            metronomeBar.progress = tempo
        }
        if (isActive) {
            startStopBtn.text = resources.getString(R.string.stop)
            stopMetronome()
            startMetronome()
        }
    }

    /**
     * Initialise the metronome controller bar by providing the min/max const values.
     * Add a listener to the bar to change the tempo of the metronome to the
     * seek bar value if the metronome is active.
     */
    private fun initSeekBar() {
        metronomeBar.min = MIN_BPM
        metronomeBar.max = MAX_BPM
        metronomeBar.progress = bpm
        metronomeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, seekVal: Int, p2: Boolean) {
                updateBpm(seekVal)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                stopMetronome()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
    }

    /**
     * Start the media player which is playing metronome clicks
     * at the tempo specified by the user.
     */
    private fun startMetronome() {
        timer = Timer()
        timer?.schedule(0, (60000 / bpm).toLong()) {
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP)
            tone.release()
        }
    }

    /**
     * Stop the media player which is playing the metronome sound.
     */
    private fun stopMetronome() {
        timer?.cancel()
    }

    /**
     * Toggle the metronome between start and stop states.
     */
    private fun toggleMetronome() {
        if (isActive) {
            stopMetronome()
            startStopBtn.text = resources.getString(R.string.start)
        } else {
            startMetronome()
            startStopBtn.text = resources.getString(R.string.stop)
        }
        isActive = !isActive
    }

    /**
     * Stop playback of the metronome when the activity stop lifecycle method is called.
     */
    override fun onStop() {
        super.onStop()
        stopMetronome()
    }

    /**
     * Save the bpm and active status of the metronome. If active,
     * the metronome will continue playing through changes such as orientation change.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isActive", isActive)
        outState.putInt("bpm", bpm)
    }

    /**
     * Check whether the metronome was active when state was saved previously.
     * If active the metronome will continue playing through changes such as orientation change.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            isActive = savedInstanceState.getBoolean("isActive")
            bpm = savedInstanceState.getInt("bpm")
        }
        updateBpm(bpm)
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
            MetronomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}