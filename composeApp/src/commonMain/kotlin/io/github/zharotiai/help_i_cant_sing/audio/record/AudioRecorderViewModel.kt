package io.github.zharotiai.help_i_cant_sing.audio.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Import the lifecycle-aware scope
import io.github.zharotiai.help_i_cant_sing.audio.detect_pitch.PitchDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AudioRecorderViewModel(
    private val recorder: AudioRecorder,
    private val pitchDetector: PitchDetector,
    private val sampleRate: Int
) : ViewModel() { // 1. It already inherits from ViewModel, which is great!

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _pitch = MutableStateFlow<Float?>(null)
    val pitch: StateFlow<Float?> = _pitch.asStateFlow()

    init {
        // 2. Observe the recorder's state to derive our own state.
        //    This creates a single source of truth.
        recorder.state
            .onEach { recordingState ->
                _isRecording.value = recordingState != RecordingState.Idle
                _isPaused.value = recordingState == RecordingState.Paused
            }
            .launchIn(viewModelScope) // Use the lifecycle-aware viewModelScope



        // 3. Observe the raw audio buffer to perform pitch detection.
        recorder.audioBufferFlow
            .onEach { buffer ->
                // This logic now only processes the buffer if we are actively recording.
                if (_isRecording.value && !_isPaused.value) {
                    val detectedPitch = pitchDetector.detectPitch(buffer, sampleRate)
                    // It will print to your Logcat every time a pitch is detected.
                    println("VIEWMODEL DEBUG: Detected pitch = $detectedPitch")

                    _pitch.value = detectedPitch
                }
            }
            .launchIn(viewModelScope) // Also use the viewModelScope
    }

    // 4. The public functions are now simple, clean delegations to the recorder.
    //    The ViewModel no longer manages the state itself; it just triggers actions.
    fun start() {
        recorder.startRecording()
    }

    fun pause() {
        recorder.pauseRecording()
    }

    fun resume() {
        recorder.resumeRecording()
    }

    fun stop() {
        recorder.stopRecording()
        _pitch.value = null // Clear the pitch when recording stops
    }
}