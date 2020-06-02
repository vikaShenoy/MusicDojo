package com.example.musicdojo

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_looper.*
import java.io.IOException

private const val DEFAULT_FILENAME = "loopRecording.mp3"

/**
 * Fragment allowing the user to record and playback their recording in a loop.
 */
class LooperFragment : Fragment() {

    private lateinit var ctx: Context

    private var isRecording: Boolean = false
    private var isPlaying: Boolean = false

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var fileName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }
        fileName = "${ctx.externalCacheDir?.absolutePath}/${DEFAULT_FILENAME}"
        return inflater.inflate(R.layout.fragment_looper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordBtn.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }
        loopBtn.setOnClickListener {
            if (!isPlaying) {
                startLooping()
            } else {
                stopLooping()
            }
        }
    }

    /**
     * Start recording audio for the user to input a loop.
     * Change the recording button color and text to indicate the user is recording.
     */
    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
            isRecording = true
            updateRecordBtn()
            Toast.makeText(ctx, resources.getString(R.string.start_recording), Toast.LENGTH_SHORT)
                .show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Release the recorder and update the recording button to indicate recording has stopped.
     */
    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        isRecording = false
        updateRecordBtn()
        Toast.makeText(ctx, resources.getString(R.string.stop_recording), Toast.LENGTH_SHORT).show()
    }

    /**
     * Start playback of the last loop the user recorded.
     */
    private fun startLooping() {
        if (isRecording) {
            Toast.makeText(ctx, resources.getString(R.string.loop_record_error), Toast.LENGTH_SHORT)
                .show()
            return
        }

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                isLooping = true
                start()
            } catch (e: IOException) {
                Log.e("tag", "Could not load recorded loop")
            }
        }
        isPlaying = true
        updateLoopBtn()
    }

    /**
     * Stop playback of the currently playing loop.
     */
    private fun stopLooping() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        updateLoopBtn()
    }

    /**
     * Update the state of the record button depending on whether the user is in
     * recording state or not.
     */
    private fun updateRecordBtn() {
        if (isRecording) {
            recordBtn.setBackgroundColor(ctx.getColor(R.color.colorRecord))
            recordBtn.text = resources.getString(R.string.stop)
        } else {
            recordBtn.setBackgroundColor(ctx.getColor(R.color.colorPrimary))
            recordBtn.text = resources.getString(R.string.record)
        }
    }

    /**
     * Update the state of the playback loop button depending on whether the
     * user is currently playing audio or not.
     */
    private fun updateLoopBtn() {
        if (isPlaying) {
            loopBtn.setBackgroundColor(ctx.getColor(R.color.colorCorrectAnswer))
            loopBtn.text = resources.getString(R.string.stop)
        } else {
            loopBtn.setBackgroundColor(ctx.getColor(R.color.colorPrimary))
            loopBtn.text = resources.getString(R.string.start)
        }
    }

    /**
     * Release the resources used by the media player and recorder.
     */
    override fun onStop() {
        super.onStop()
        mediaRecorder?.release()
        mediaPlayer?.release()
        mediaRecorder = null
        mediaPlayer = null
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
            LooperFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}