@file:OptIn(ExperimentalForeignApi::class)

package io.github.zharotiai.help_i_cant_sing.audio

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.*
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.*

class IosAudioRecorder(private val outputFile: String) : AudioRecorder {
    private var recorder: AVAudioRecorder? = null
    override var isRecording: Boolean = false
        private set
    override var isPaused: Boolean = false
        private set

    override fun startRecording() {
        val settings = mapOf(
            AVFormatIDKey to kAudioFormatMPEG4AAC,
            AVSampleRateKey to 44100.0,
            AVNumberOfChannelsKey to 1,
            AVEncoderAudioQualityKey to AVAudioQualityHigh
        )
        val url = NSURL.fileURLWithPath(outputFile)
        recorder = AVAudioRecorder(url, settings as Map<Any?, Any?>, null).apply {
            prepareToRecord()
            record()
        }
        isRecording = true
        isPaused = false
    }

    override fun pauseRecording() {
        recorder?.pause()
        isPaused = true
    }

    override fun resumeRecording() {
        recorder?.record()
        isPaused = false
    }

    override fun stopRecording() {
        recorder?.stop()
        recorder = null
        isRecording = false
        isPaused = false
    }
}
