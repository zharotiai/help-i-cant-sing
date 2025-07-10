package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel
import kotlin.math.log2
import kotlin.math.roundToInt

@Composable
fun MyPitchScreen(viewModel: AudioRecorderViewModel? = null) {
    if (viewModel == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Preview mode: ViewModel not available.")
        }
        return
    }

    val rawPitch by viewModel.pitch.collectAsState(null)
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    var smoothedPitch by remember { mutableStateOf<Float?>(null) }
    val pitchHistory = remember { mutableStateListOf<Float?>() }

    // Smooth the pitch to prevent a glitchy line
    LaunchedEffect(rawPitch) {
        val currentRawPitch = rawPitch
        val currentSmoothedPitch = smoothedPitch
        if (currentRawPitch == null) {
            smoothedPitch = null
            return@LaunchedEffect
        }
        if (currentSmoothedPitch == null) {
            smoothedPitch = currentRawPitch
        } else {
            val smoothingFactor = 0.2f
            smoothedPitch = smoothingFactor * currentRawPitch + (1.0f - smoothingFactor) * currentSmoothedPitch
        }
    }

    LaunchedEffect(smoothedPitch, isRecording, isPaused) {
        if (isRecording && !isPaused) {
            pitchHistory.add(smoothedPitch)
        } else if (!isRecording && pitchHistory.isNotEmpty()) {
            pitchHistory.clear()
            smoothedPitch = null
        }
    }

    // --- HOISTED STATE & LAYOUT PARAMETERS ---
    val verticalScrollState = rememberScrollState()
    val dpPerOctave = 320.dp

    val minFreq = remember { notes.values.minOrNull() ?: 27.50f }
    val maxFreq = remember { notes.values.maxOrNull() ?: 4186.01f }
    val numOctaves = remember(minFreq, maxFreq) { log2(maxFreq / minFreq) }
    val totalCanvasHeight = remember(numOctaves) { (dpPerOctave.value * numOctaves).dp }

    val density = LocalDensity.current
    val freqToY: (Float) -> Float = remember(totalCanvasHeight, dpPerOctave, minFreq, density) {
        val canvasHeightPx = with(density) { totalCanvasHeight.toPx() }
        val pixelsPerOctave = with(density) { dpPerOctave.toPx() }
        val logMin = log2(minFreq)

        return@remember { freq ->
            val logFreq = log2(freq.coerceAtLeast(0.1f))
            val octavesFromMin = logFreq - logMin
            canvasHeightPx - (octavesFromMin * pixelsPerOctave)
        }
    }

    // This effect now lives here and controls the SHARED vertical scroll state.
    LaunchedEffect(smoothedPitch) {
        smoothedPitch?.let { currentPitch ->
            val pitchY = freqToY(currentPitch)
            val viewportHeight = verticalScrollState.viewportSize
            if (viewportHeight > 0) {
                val targetScrollY = pitchY - (viewportHeight / 2f)
                verticalScrollState.animateScrollTo(targetScrollY.roundToInt())
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The main content area is now a Row to place items side-by-side.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Pitch Graph takes up most of the space.
            PitchGraph(
                pitch = smoothedPitch,
                pitchHistory = pitchHistory,
                modifier = Modifier.weight(1f),
                // Pass down the hoisted state
                verticalScrollState = verticalScrollState,
                totalCanvasHeight = totalCanvasHeight,
                freqToY = freqToY
            )

            // Piano Keyboard takes up a fixed width on the right.
            PianoKeyboard(
                currentPitch = smoothedPitch,
                modifier = Modifier.width(50.dp),
                // Pass down the SAME hoisted state for perfect sync
                verticalScrollState = verticalScrollState,
                totalHeight = totalCanvasHeight,
                dpPerOctave = dpPerOctave,
                freqToY = freqToY
            )
        }

        Divider(
            color = MaterialTheme.colorScheme.onSurface,
            thickness = 4.dp,
            modifier = Modifier.fillMaxWidth()
        )

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