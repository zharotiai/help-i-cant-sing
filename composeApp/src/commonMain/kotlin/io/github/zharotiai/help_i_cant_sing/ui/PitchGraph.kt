package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import io.github.zharotiai.help_i_cant_sing.ui.theme.AppTheme
import kotlin.math.*

// A map of musical notes to their fundamental frequencies in Hertz.
private val notes = mapOf(
    "A0" to 27.50f, "A#0" to 29.14f, "B0" to 30.87f,
    "C1" to 32.70f, "C#1" to 34.65f, "D1" to 36.71f, "D#1" to 38.89f, "E1" to 41.20f, "F1" to 43.65f, "F#1" to 46.25f, "G1" to 49.00f, "G#1" to 51.91f,
    "A1" to 55.00f, "A#1" to 58.27f, "B1" to 61.74f,
    "C2" to 65.41f, "C#2" to 69.30f, "D2" to 73.42f, "D#2" to 77.78f, "E2" to 82.41f, "F2" to 87.31f, "F#2" to 92.50f, "G2" to 97.99f, "G#2" to 103.83f,
    "A2" to 110.00f, "A#2" to 116.54f, "B2" to 123.47f,
    "C3" to 130.81f, "C#3" to 138.59f, "D3" to 146.83f, "D#3" to 155.56f, "E3" to 164.81f, "F3" to 174.61f, "F#3" to 185.00f, "G3" to 196.00f, "G#3" to 207.65f,
    "A3" to 220.00f, "A#3" to 233.08f, "B3" to 246.94f,
    "C4" to 261.63f, "C#4" to 277.18f, "D4" to 293.66f, "D#4" to 311.13f, "E4" to 329.63f, "F4" to 349.23f, "F#4" to 369.99f, "G4" to 392.00f, "G#4" to 415.30f,
    "A4" to 440.00f, "A#4" to 466.16f, "B4" to 493.88f,
    "C5" to 523.25f, "C#5" to 554.37f, "D5" to 587.33f, "D#5" to 622.25f, "E5" to 659.25f, "F5" to 698.46f, "F#5" to 739.99f, "G5" to 783.99f, "G#5" to 830.61f,
    "A5" to 880.00f, "A#5" to 932.33f, "B5" to 987.77f,
    "C6" to 1046.50f, "C#6" to 1108.73f, "D6" to 1174.66f, "D#6" to 1244.51f, "E6" to 1318.51f, "F6" to 1396.91f, "F#6" to 1479.98f, "G6" to 1567.98f, "G#6" to 1661.22f,
    "A6" to 1760.00f, "A#6" to 1864.66f, "B6" to 1975.53f,
    "C7" to 2093.00f, "C#7" to 2217.46f, "D7" to 2349.32f, "D#7" to 2489.02f, "E7" to 2637.02f, "F7" to 2793.83f, "F#7" to 2959.96f, "G7" to 3135.96f, "G#7" to 3322.44f,
    "A7" to 3520.00f, "A#7" to 3729.31f, "B7" to 3951.07f,
    "C8" to 4186.01f
)


@Composable
fun PitchGraph(
    pitch: Float? = null,
    pitchHistory: List<Float?> = emptyList(),
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surface

    // --- NEW: Define the vertical scale of the graph ---
    // This determines how "zoomed in" the y-axis is.
    // 320.dp means one octave will take up 320dp of vertical space.
    val dpPerOctave = 320.dp

    val minFreq = notes.values.minOrNull() ?: 27.50f // A0
    val maxFreq = notes.values.maxOrNull() ?: 4186.01f // C8

    // --- NEW: Calculate the total height of the scrollable canvas ---
    val numOctaves = log2(maxFreq / minFreq)
    val totalCanvasHeight = (dpPerOctave.value * numOctaves).dp

    // --- NEW: The graph is now wrapped in a scrollable Box ---
    Box(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Canvas(
            // The Canvas is now the full height of all octaves and fills the width
            modifier = Modifier.fillMaxWidth().height(totalCanvasHeight)
        ) {
            val width = size.width
            val height = size.height // This is now the total canvas height in pixels
            val pixelsPerOctave = dpPerOctave.toPx()
            val logMin = log2(minFreq)

            // --- NEW: Updated function to convert frequency to a y-coordinate on our tall canvas ---
            fun freqToY(freq: Float): Float {
                // Coerce at least a small value to prevent log2(0) which is -Infinity
                val logFreq = log2(freq.coerceAtLeast(0.1f))
                val octavesFromMin = logFreq - logMin
                // Calculate Y position from the top of the canvas
                return height - (octavesFromMin * pixelsPerOctave)
            }

            // Draw grid lines for all notes
            notes.forEach { (noteName, freq) ->
                val y = freqToY(freq)
                val isCNote = noteName.length >= 2 && noteName[0] == 'C' && noteName[1].isDigit()

                drawLine(
                    color = Color.Gray.copy(alpha = if (isCNote) 0.7f else 0.3f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = if (isCNote) 2.dp.toPx() else 1.dp.toPx()
                )
            }

            // Draw the pitch history
            if (pitchHistory.isNotEmpty()) {
                val path = Path()
                val historySize = pitchHistory.size
                val stepX = width / (historySize - 1).coerceAtLeast(1)

                var lastValidFreq: Float? = null
                pitchHistory.forEachIndexed { index, freq ->
                    if (freq != null) {
                        val x = index * stepX
                        val y = freqToY(freq)
                        if (lastValidFreq == null) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                        lastValidFreq = freq
                    } else {
                        lastValidFreq = null // Lift the pen for null gaps
                    }
                }

                drawPath(
                    path = path,
                    color = lineColor.copy(alpha = 0.7f),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Draw the current pitch indicator
            val toleranceCents = 10.0
            pitch?.let { currentPitch ->
                val y = freqToY(currentPitch)

                val nearestMidi = freqToMidi(currentPitch)
                val targetPitchFreq = midiToFreq(nearestMidi)
                val centsOff = 1200 * log2(currentPitch / targetPitchFreq)

                val color = if (abs(centsOff) <= toleranceCents) Color.Green else Color.Red

                drawCircle(
                    color = color,
                    radius = 5.dp.toPx(),
                    center = Offset(width - 1.dp.toPx(), y) // Draw slightly inside the edge
                )
            }



            // Draw a static vertical line at the right edge
            drawLine(
                color = lineColor,
                start = Offset(width, 0f),
                end = Offset(width, height),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}


@Preview
@Composable
fun PreviewPitchGraph() {
    val samplePitch = 440f // A4
    val sampleHistory = List(200) { index ->
        val frequency = 440f * 2f.pow(sin(index * 0.1f) * 0.7f + cos(index * 0.05f) * 0.5f)
        frequency.takeIf { it.isFinite() }
    }

    AppTheme {
        // --- NEW: The PitchGraph is now self-contained and scrollable. ---
        // We just need to give it a size to occupy in the layout.
        PitchGraph(
            pitch = samplePitch,
            pitchHistory = sampleHistory,
            modifier = Modifier.fillMaxSize()
        )
    }
}