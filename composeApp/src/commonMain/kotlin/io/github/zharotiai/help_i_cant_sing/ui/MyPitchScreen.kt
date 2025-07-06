package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.unit.dp
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MyPitchScreen(viewModel: AudioRecorderViewModel? = null) {
    // 2. Handle the null case for Previews.
    //    If the viewModel is null, we're in a preview, so show a placeholder and exit.
    if (viewModel == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Preview mode: ViewModel not available.")
        }
        return // Stop execution here for the preview
    }

    // This code will now only run when a real ViewModel is provided (i.e., in the actual app).
    val pitch by viewModel.pitch.collectAsState(null)
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val pitchHistory = remember { mutableStateListOf<Float?>() }
    val maxHistory = 300

    LaunchedEffect(pitch, isRecording, isPaused) {
        if (isRecording && !isPaused) {
            pitchHistory.add(pitch)
            if (pitchHistory.size > maxHistory) {
                pitchHistory.removeAt(0)
            }
        } else if (!isRecording && pitchHistory.isNotEmpty()) {
            pitchHistory.clear()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PitchGraph(
            pitch = pitch,
            pitchHistory = pitchHistory,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Divider(
            color = MaterialTheme.colorScheme.onSurface,
            thickness = 4.dp,         // Make it bolder/thicker
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Simplified pause/resume logic for better clarity.
        RecordButton(
            onRecord = { viewModel.start() },
            onPause = { if (isPaused) viewModel.resume() else viewModel.pause() },
            onStop = { viewModel.stop() },
            isRecording = isRecording,
            isPaused = isPaused,
            modifier = Modifier.padding(horizontal = 64.dp, vertical = 16.dp)
        )
    }
}