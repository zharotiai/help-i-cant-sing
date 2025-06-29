package io.github.zharotiai.help_i_cant_sing.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AndroidAudioRecorder(private val context: android.content.Context) : AudioRecorder {
    // On Android we require a context object
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var audioRecord: AudioRecord? = null
    private val _state = MutableStateFlow(RecordingState.Idle)
    override val state: StateFlow<RecordingState> = _state.asStateFlow()
    private val _audioBufferFlow = MutableSharedFlow<ShortArray>(extraBufferCapacity = 8)
    override val audioBufferFlow: Flow<ShortArray> = _audioBufferFlow.asSharedFlow()
    private var recordingJob: kotlinx.coroutines.Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private fun startRecordingJob() {
        recordingJob = scope.launch {
            val buffer = ShortArray(bufferSize)
            while (_state.value == RecordingState.Recording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    _audioBufferFlow.emit(buffer.copyOf(read))
                }
            }
        }
    }

    override fun startRecording() {
        if (_state.value == RecordingState.Recording) return
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, handle appropriately
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        audioRecord?.startRecording()
        _state.value = RecordingState.Recording
        startRecordingJob()
    }

    override fun pauseRecording() {
        if (_state.value != RecordingState.Recording) return
        _state.value = RecordingState.Paused
        audioRecord?.stop()
        recordingJob?.cancel()
    }

    override fun resumeRecording() {
        if (_state.value != RecordingState.Paused) return
        audioRecord?.startRecording()
        _state.value = RecordingState.Recording
        startRecordingJob()
    }

    override fun stopRecording() {
        _state.value = RecordingState.Idle
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordingJob?.cancel()
    }
}
