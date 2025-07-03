package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel

@Composable
fun MyPitchScreen(viewModel: AudioRecorderViewModel) {
    val pitch by viewModel.pitch.collectAsState(null)
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val pitchHistory = remember { mutableStateListOf<Float?>() }
    val maxHistory = 300 // Increased history size for longer scrolling
}