package com.example.musicdojo

import android.content.Context
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
import androidx.fragment.app.Fragment
import com.example.musicdojo.util.INITIAL_BPM
import com.example.musicdojo.util.MAX_BPM
import com.example.musicdojo.util.MIN_BPM
import com.example.musicdojo.util.TEMPO_BUTTONS
import kotlinx.android.synthetic.main.fragment_metronome.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.sqrt

class MetronomeFragment : Fragment(), SensorEventListener {

    private lateinit var ctx: Context
    private var timer = Timer()
    private var bpm: Int = INITIAL_BPM
    private var isActive = false

    private var lastTime: Long = 0
    private val TIMEOUT = 1000

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }
        sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
            accelerometer = it
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
        return inflater.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bpmTxt.text = bpm.toString()
        initSeekBar()
        initTempoButtons()
        startStopBtn.setOnClickListener {
            toggleMetronome()
        }
    }

    private fun initTempoButtons() {
        initTempoButton(btn1, TEMPO_BUTTONS[0])
        initTempoButton(btn2, TEMPO_BUTTONS[1])
        initTempoButton(btn3, TEMPO_BUTTONS[2])
        initTempoButton(btn4, TEMPO_BUTTONS[3])
    }

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

    private fun updateBpm(tempo: Int) {
        bpm = tempo
        bpmTxt.text = bpm.toString()
        if (isActive) {
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
        timer.schedule(0, (60000 / bpm).toLong()) {
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP)
            tone.release()
        }
    }

    /**
     * Stop the media player which is playing the metronome sound.
     */
    private fun stopMetronome() {
        timer.cancel()
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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTime > TIMEOUT) {
                    lastTime = currentTime
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    Log.i("test", "x: ${x.toString()}")
                    Log.i("test", "y: ${y.toString()}")
                    Log.i("test", "z: ${z.toString()}")

                    if (z > 15) {
                        updateBpm(bpm + TEMPO_BUTTONS.last())
                    } else if (z < 0) {
                        updateBpm(bpm + TEMPO_BUTTONS[0])
                    }

                }
            }
        }

    }
}