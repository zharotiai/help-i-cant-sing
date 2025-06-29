package io.github.zharotiai.help_i_cant_sing

import androidx.compose.ui.window.ComposeUIViewController
import io.github.zharotiai.help_i_cant_sing.audio.AudioRecorderViewModel
import io.github.zharotiai.help_i_cant_sing.audio.IosAudioRecorder
import io.github.zharotiai.help_i_cant_sing.permissions.PermissionManager
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val recorder = IosAudioRecorder(outputFile = "recording.m4a")
    val viewModel = AudioRecorderViewModel(recorder)
    val permissionManager = PermissionManager()

    return ComposeUIViewController {
        App(viewModel = viewModel, permissionManager = permissionManager)
    }
}