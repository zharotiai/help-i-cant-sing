package io.github.zharotiai.help_i_cant_sing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import help_i_cant_sing.composeapp.generated.resources.Res
import help_i_cant_sing.composeapp.generated.resources.compose_multiplatform
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel
import androidx.compose.runtime.collectAsState
import io.github.zharotiai.help_i_cant_sing.permissions.PermissionManager

@Composable
fun AudioRecorderControls(viewModel: AudioRecorderViewModel) {
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!isRecording) {
            Button(onClick = { viewModel.start() }) { Text("Start Recording") }
        } else {
            if (!isPaused) {
                Button(onClick = { viewModel.pause() }) { Text("Pause") }
            } else {
                Button(onClick = { viewModel.resume() }) { Text("Resume") }
            }
            Button(onClick = { viewModel.stop() }) { Text("Stop") }
        }
    }
}

@Composable
fun PitchDisplay(viewModel: AudioRecorderViewModel) {
    val pitch by viewModel.pitch.collectAsState()
    val roundedPitch = pitch?.let { (it * 100).toInt() / 100.0 }?.toString() ?: "--"
    val text = "Current pitch: $roundedPitch Hz"
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
}

@Composable
@Preview
fun App(viewModel: AudioRecorderViewModel? = null, permissionManager: PermissionManager? = null) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        // Request audio permission on activation (only if permissionManager is provided)
        LaunchedEffect(permissionManager) {
            permissionManager?.requestAudioPermission {}
        }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
            // Add audio recorder controls and pitch display if viewModel is provided
            viewModel?.let {
                PitchDisplay(it)
                AudioRecorderControls(it)
            }
        }
    }
}