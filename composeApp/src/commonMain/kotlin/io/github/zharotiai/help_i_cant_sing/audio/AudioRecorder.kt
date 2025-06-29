package io.github.zharotiai.help_i_cant_sing.audio

import kotlinx.coroutines.flow.Flow

enum class RecordingState {
    Idle, Recording, Paused
}

interface AudioRecorder {
    fun startRecording()
    fun pauseRecording()
    fun resumeRecording()
    fun stopRecording()
    val state: Flow<RecordingState>
    val audioBufferFlow: Flow<ShortArray> // Emits raw PCM audio buffers in real time
}
