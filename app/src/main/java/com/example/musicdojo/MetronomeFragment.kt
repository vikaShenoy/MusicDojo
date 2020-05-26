package com.example.musicdojo

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.musicdojo.util.INITIAL_BPM
import com.example.musicdojo.util.MAX_BPM
import com.example.musicdojo.util.MIN_BPM
import kotlinx.android.synthetic.main.fragment_metronome.*
import kotlinx.android.synthetic.main.fragment_metronome.view.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MetronomeFragment : Fragment() {

    private lateinit var ctx: Context
    private var player: MediaPlayer? = null
    private var timer = Timer()
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
        return inflater.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        player = MediaPlayer.create(ctx, R.raw.metronome)
        bpmTxt.text = bpm.toString()
        initSeekBar()
        startStopBtn.setOnClickListener {
            if (isActive) {
                stopMetronome()
                startStopBtn.text = resources.getString(R.string.start)
            } else {
                startMetronome()
                startStopBtn.text = resources.getString(R.string.stop)
            }
            isActive = !isActive
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
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                bpm = progress
                bpmTxt.text = bpm.toString()
                if (isActive) {
                    stopMetronome()
                    startMetronome()
                }
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
        player = MediaPlayer.create(ctx, R.raw.metronome)
        timer.schedule(0, (60000 / bpm).toLong()) {
            player?.seekTo(0)
            player?.start()
        }
    }

    /**
     * Stop the media player which is playing the metronome sound.
     */
    private fun stopMetronome() {
        timer.cancel()
        releasePlayer()
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
            MetronomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}