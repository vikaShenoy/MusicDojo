package com.example.musicdojo

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_metronome.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MetronomeFragment : Fragment() {

    private var player: MediaPlayer? = null
    private lateinit var ctx: Context

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

        startStopBtn.setOnClickListener {
            // TODO - get the seek bar value here
            val bpm = 165
            startMetronome(bpm)
        }
    }

    /**
     * Start the media player which is playing metronome clicks
     * at the tempo specified by the user.
     * @param bpm: Metronome tempo in BPM
     */
    private fun startMetronome(bpm: Int) {
        Timer().schedule(0, (60000 / bpm).toLong()) {
            player?.seekTo(0)
            player?.start()
        }

    }

    /**
     * Stop the mp which is playing the metronome sound.
     */
    private fun stopMetronome() {
        player?.stop()
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