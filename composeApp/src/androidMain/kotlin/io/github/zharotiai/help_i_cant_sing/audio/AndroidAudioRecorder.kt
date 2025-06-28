package io.github.zharotiai.help_i_cant_sing.audio

import android.media.MediaRecorder
import java.io.File

class AndroidAudioRecorder(private val outputFile: File) : AudioRecorder {
    private var recorder: MediaRecorder? = null
    override var isRecording: Boolean = false
        private set
    override var isPaused: Boolean = false
        private set

    override fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        isRecording = true
        isPaused = false
    }

    override fun pauseRecording() {
        recorder?.pause()
        isPaused = true
    }

    override fun resumeRecording() {
        recorder?.resume()
        isPaused = false
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        isPaused = false
    }
}

