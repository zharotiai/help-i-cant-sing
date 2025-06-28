package io.github.zharotiai.help_i_cant_sing.permissions

import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import platform.AVFAudio.AVAudioSessionRecordPermissionUndetermined

class ApplePermissionHandler : PermissionHandler {
    override fun isAudioPermissionGranted(): Boolean {
        return AVAudioSession.sharedInstance().recordPermission() == AVAudioSessionRecordPermissionGranted
    }

    override fun shouldShowRationale(): Boolean {
        // iOS does not provide a direct equivalent to Android's rationale
        return false
    }

    override fun requestAudioPermission(onResult: (Boolean) -> Unit) {
        val session = AVAudioSession.sharedInstance()
        when (session.recordPermission()) {
            AVAudioSessionRecordPermissionGranted -> onResult(true)
            AVAudioSessionRecordPermissionDenied -> onResult(false)
            AVAudioSessionRecordPermissionUndetermined -> session.requestRecordPermission { granted ->
                onResult(granted)
            }
            else -> onResult(false)
        }
    }
}

actual class PermissionManager actual constructor() {
    private val handler = ApplePermissionHandler()
    actual fun isAudioPermissionGranted(): Boolean = handler.isAudioPermissionGranted()
    actual fun shouldShowRationale(): Boolean = handler.shouldShowRationale()
    actual fun requestAudioPermission(onResult: (Boolean) -> Unit) = handler.requestAudioPermission(onResult)
}