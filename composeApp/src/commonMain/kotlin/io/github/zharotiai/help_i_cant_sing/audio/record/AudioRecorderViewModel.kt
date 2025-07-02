package io.github.zharotiai.help_i_cant_sing.audio.record

import io.github.zharotiai.help_i_cant_sing.audio.detect_pitch.PitchDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioRecorderViewModel(
    private val recorder: AudioRecorder,
    pitchDetector: PitchDetector,
    private val sampleRate: Int
) {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _pitch = MutableStateFlow<Float?>(null)
    val pitch: StateFlow<Float?> = _pitch.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    var pitchDetector: PitchDetector = pitchDetector
        set(value) {
            field = value
            // Optionally, you could reset pitch or trigger other logic here
        }

    init {
        scope.launch {
            recorder.audioBufferFlow.collect { buffer ->
                val detectedPitch = pitchDetector.detectPitch(buffer, sampleRate)
                _pitch.value = detectedPitch
            }
        }
    }

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