package io.github.zharotiai.help_i_cant_sing.audio

interface AudioRecorder {
    fun startRecording()
    fun pauseRecording()
    fun resumeRecording()
    fun stopRecording()
    val isRecording: Boolean
    val isPaused: Boolean
}

