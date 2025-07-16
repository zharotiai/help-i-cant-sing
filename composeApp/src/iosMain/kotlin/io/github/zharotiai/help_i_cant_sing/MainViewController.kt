package io.github.zharotiai.help_i_cant_sing

import androidx.compose.ui.window.ComposeUIViewController
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel
import io.github.zharotiai.help_i_cant_sing.audio.IosAudioRecorder
import io.github.zharotiai.help_i_cant_sing.audio.SAMPLE_RATE
import io.github.zharotiai.help_i_cant_sing.audio.detect_pitch.YIN
import io.github.zharotiai.help_i_cant_sing.permissions.PermissionManager
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val recorder = IosAudioRecorder()
    val pitchDetector = YIN()
    val viewModel = AudioRecorderViewModel(recorder, pitchDetector, SAMPLE_RATE)
    val permissionManager = PermissionManager()

    return ComposeUIViewController {
        App(viewModel = viewModel, permissionManager = permissionManager)
    }
}