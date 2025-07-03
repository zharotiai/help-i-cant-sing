package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel
import kotlinx.coroutines.delay
import kotlin.math.*


private const val BACKGROUND_COLOUR = 0xFF1E1E1E

@Composable
fun LivePitchScreen(viewModel: AudioRecorderViewModel) {
    val pitch by viewModel.pitch.collectAsState(null)
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val pitchHistory = remember { mutableStateListOf<Float?>() }
    val maxHistory = 300 // Increased history size for longer scrolling

    // Full piano range constants
    val minFreq = 27.5f  // A0
    val maxFreq = 4186f  // C8

    // Rolling pitch history
    LaunchedEffect(Unit) {
        while (true) {
            if (isRecording && !isPaused) {
                if (pitchHistory.size >= maxHistory) {
                    pitchHistory.removeFirst()
                }
                pitchHistory.add(pitch)
                delay(32) // ~30fps update rate
            } else {
                delay(100)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(BACKGROUND_COLOUR))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content area with graph and keyboard
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    RollingPitchGraph(
                        pitchHistory = pitchHistory,
                        minFreq = minFreq,
                        maxFreq = maxFreq,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight()
                    ) {
                        VerticalPianoKeyboard(
                            currentPitch = pitch,
                            minFreq = minFreq,
                            maxFreq = maxFreq
                        )
                    }
                }

                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    // Note display
                    if (pitch != null) {
                        Surface(
                            color = Color(0xFF4285F4).copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = midiToNoteName(freqToMidi(pitch!!)),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // Status overlay for when not recording
                if (!isRecording) {
                    Surface(
                        color = Color(0x88000000),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tap play to start recording",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                        )
                    }
                }
            }

            // Recording controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause Button
                if (isRecording) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                if (isPaused) viewModel.resume() else viewModel.pause()
                            },
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (isPaused) "Resume" else "Pause",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { viewModel.start() },
                        color = Color(0xFF4CAF50),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Play",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                }

                // Stop Button (only show when recording)
                if (isRecording) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                viewModel.stop()
                                pitchHistory.clear() // Clear history on stop
                            },
                        color = Color(0xFFF44336),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Stop",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerticalPianoKeyboard(
    currentPitch: Float?,
    minFreq: Float,
    maxFreq: Float
) {
    val minMidi = freqToMidi(minFreq)  // A0 = 21
    val maxMidi = freqToMidi(maxFreq)  // C8 = 108
    val currentMidi = currentPitch?.let { freqToMidi(it) }
    val whiteKeys = listOf(0, 2, 4, 5, 7, 9, 11)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            for (midi in maxMidi downTo minMidi) {
                val isWhite = (midi % 12) in whiteKeys
                val isCurrent = midi == currentMidi
                val isC = midi % 12 == 0

                Box(
                    modifier = Modifier
                        .height(24.dp)  // Much bigger keys to show ~2.5 octaves
                        .fillMaxWidth(if (isWhite) 1f else 0.65f)
                ) {
                    // Key background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                when {
                                    isCurrent -> Color(0xFF4285F4)
                                    isWhite -> Color(0xFFF8FAFF)
                                    else -> Color(0xFF9AA0A6)
                                },
                                RoundedCornerShape(4.dp)
                            )
                    )

                    // Octave label (for all keys, but only visible on white keys)
                    if (isWhite) {
                        Text(
                            text = "${midi / 12 - 1}",
                            color = Color(0x40000000),  // Subtle dark overlay
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))  // Gap between keys
            }
        }
    }
}

@Composable
private fun RollingPitchGraph(
    pitchHistory: List<Float?>,
    minFreq: Float,
    maxFreq: Float,
    modifier: Modifier = Modifier
) {
    val offsetAnim = remember { Animatable(0f) }
    val currentPitch = pitchHistory.lastOrNull()
    val minMidi = freqToMidi(minFreq)
    val maxMidi = freqToMidi(maxFreq)
    val midiRange = maxMidi - minMidi + 1

    // Continuous scrolling animation
    LaunchedEffect(Unit) {
        while (true) {
            offsetAnim.animateTo(
                targetValue = -1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val pointSpacing = width / 150f
        val keyHeight = height / midiRange

        // Draw background grid lines for each note
        for (midi in minMidi..maxMidi) {
            val isWhiteKey = (midi % 12) in listOf(0, 2, 4, 5, 7, 9, 11)
            val y = (maxMidi - midi) * keyHeight

            drawLine(
                color = if (isWhiteKey) Color(0xFF2A2A2A) else Color(0xFF222222),
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(width, y),
                strokeWidth = if (isWhiteKey) 1f else 0.5f
            )
        }

        // Highlight the current note's full width
        if (currentPitch != null) {
            val currentMidi = freqToMidi(currentPitch)
            val y = (maxMidi - currentMidi) * keyHeight
            drawRect(
                color = Color(0x1F4285F4),
                topLeft = androidx.compose.ui.geometry.Offset(0f, y - keyHeight/2),
                size = androidx.compose.ui.geometry.Size(width, keyHeight)
            )
        }

        // Draw the pitch line
        val path = Path()
        var firstPoint = true

        pitchHistory.asReversed().forEachIndexed { index, pitch ->
            val x = width - (index * pointSpacing) + (offsetAnim.value * pointSpacing)
            val y = pitch?.let {
                val midi = freqToMidi(it)
                (maxMidi - midi) * keyHeight
            } ?: (height / 2)  // Default to middle when no pitch

            if (firstPoint) {
                path.moveTo(x, y)
                firstPoint = false
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw the path with a thicker stroke
        drawPath(
            path = path,
            color = Color(0xFF64B5F6),
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}


fun midiToNoteName(midi: Int): String {
    val notes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val note = notes[midi % 12]
    val octave = (midi / 12) - 1
    return "$note$octave"
}
