@file:OptIn(ExperimentalForeignApi::class)

package io.github.zharotiai.help_i_cant_sing.audio

import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.AVFAudio.*
import platform.AVFoundation.*
import platform.Foundation.*
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze

class IosAudioRecorder : AudioRecorder { // doesn't required a context object
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _state = MutableStateFlow(RecordingState.Idle)
    override val state: Flow<RecordingState> = _state.asStateFlow()
    private val _audioBufferFlow = MutableSharedFlow<ShortArray>(extraBufferCapacity = 8)
    override val audioBufferFlow: Flow<ShortArray> = _audioBufferFlow.asSharedFlow()

    private var engine: AVAudioEngine? = null
    private var inputNode: AVAudioInputNode? = null
    // Removed format property
    var isRecording: Boolean = false
        private set
    var isPaused: Boolean = false
        private set

    private fun ensureMicrophonePermission(onGranted: (Boolean) -> Unit) {
        val session = AVAudioSession.sharedInstance()
        when (session.recordPermission) {
            AVAudioSessionRecordPermissionGranted -> onGranted(true)
            AVAudioSessionRecordPermissionDenied -> {
                val alert = UIAlertController.alertControllerWithTitle(
                    title = "Microphone Permission Required",
                    message = "Please enable microphone access in Settings > Privacy > Microphone.",
                    preferredStyle = UIAlertControllerStyleAlert
                )
                alert.addAction(
                    UIAlertAction.actionWithTitle(
                    title = "OK",
                    style = UIAlertActionStyleDefault,
                    handler = null
                ))
                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootVC?.presentViewController(alert, animated = true, completion = null)
                onGranted(false)
            }
            AVAudioSessionRecordPermissionUndetermined -> {
                session.requestRecordPermission { granted ->
                    if (!granted) {
                        val alert = UIAlertController.alertControllerWithTitle(
                            title = "Microphone Permission Required",
                            message = "Please enable microphone access in Settings > Privacy > Microphone.",
                            preferredStyle = UIAlertControllerStyleAlert
                        )
                        alert.addAction(UIAlertAction.actionWithTitle(
                            title = "OK",
                            style = UIAlertActionStyleDefault,
                            handler = null
                        ))
                        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                        rootVC?.presentViewController(alert, animated = true, completion = null)
                    }
                    onGranted(granted)
                }
            }
            else -> onGranted(false)
        }
    }

    override fun startRecording() {
        ensureMicrophonePermission { granted ->
            if (!granted) return@ensureMicrophonePermission
            if (_state.value == RecordingState.Recording) return@ensureMicrophonePermission
            // Set and activate AVAudioSession before starting engine
            val session = AVAudioSession.sharedInstance()
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                session.setCategory(AVAudioSessionCategoryPlayAndRecord, error = errorPtr.ptr)
                session.setActive(true, error = errorPtr.ptr)
            }
            engine = AVAudioEngine()
            inputNode = engine?.inputNode
            val bufferSize: UInt = 2048u
            inputNode?.removeTapOnBus(0u) // Remove any previous tap
            val hwFormat = inputNode?.inputFormatForBus(0u)
            if (hwFormat != null && inputNode != null) {
                inputNode!!.installTapOnBus(0u, bufferSize, hwFormat) { buffer, _ ->
                    val audioBuffer = buffer as AVAudioPCMBuffer
                    val channelData = audioBuffer.int16ChannelData
                    val frameLength = audioBuffer.frameLength.toInt()
                    if (channelData != null && frameLength > 0) {
                        val shortPointer = channelData[0]!!
                        val shortArray = ShortArray(frameLength) { i -> shortPointer[i] }
                        scope.launch { _audioBufferFlow.emit(shortArray) }
                    }
                }
                val errorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
                val started = engine?.startAndReturnError(errorPtr.ptr) ?: false
                if (!started) {
                    println("AVAudioEngine failed to start: ${'$'}{errorPtr.value?.localizedDescription}")
                    inputNode!!.removeTapOnBus(0u)
                    engine = null
                    inputNode = null
                    _state.value = RecordingState.Idle
                    nativeHeap.free(errorPtr)
                    return@ensureMicrophonePermission
                }
                nativeHeap.free(errorPtr)
                _state.value = RecordingState.Recording
            }
        }
    }

    override fun pauseRecording() {
        if (_state.value != RecordingState.Recording) return
        engine?.pause()
        _state.value = RecordingState.Paused
    }

    override fun resumeRecording() {
        ensureMicrophonePermission { granted ->
            if (!granted) return@ensureMicrophonePermission
            if (_state.value != RecordingState.Paused) return@ensureMicrophonePermission
            engine?.prepare()
            engine?.startAndReturnError(null)
            _state.value = RecordingState.Recording
        }
    }

    override fun stopRecording() {
        engine?.stop()
        inputNode?.removeTapOnBus(0u)
        engine = null
        inputNode = null
        _state.value = RecordingState.Idle
    }
}
