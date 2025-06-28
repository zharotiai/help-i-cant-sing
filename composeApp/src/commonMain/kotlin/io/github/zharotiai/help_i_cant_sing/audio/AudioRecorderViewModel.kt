package io.github.zharotiai.help_i_cant_sing.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioRecorderViewModel(private val recorder: AudioRecorder) {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    fun start() {
        recorder.startRecording()
        _isRecording.value = true
        _isPaused.value = false
    }

    fun pause() {
        recorder.pauseRecording()
        _isPaused.value = true
    }

    fun resume() {
        recorder.resumeRecording()
        _isPaused.value = false
    }

    fun stop() {
        recorder.stopRecording()
        _isRecording.value = false
        _isPaused.value = false
    }
}

