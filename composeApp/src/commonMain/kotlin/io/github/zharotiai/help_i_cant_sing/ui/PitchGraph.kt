package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.*

@Composable
fun PitchGraph(
    pitch: Float? = null,
    pitchHistory: List<Float?> = emptyList(),
    modifier: Modifier = Modifier
) {
    // Color scheme
    val backgroundColor = Color(0xFF1E1E1E)  // Dark background matching RecordButton
    val gridLineLight = Color(0xFF2A2A2A)    // Light grid lines
    val gridLineDark = Color(0xFF222222)     // Dark grid lines
    val pitchLineColor = Color(0xFF64B5F6)   // Light blue for pitch line
    val currentPitchColor = Color(0xFFFF9800) // Orange accent (matching pause button)
    val highlightColor = Color(0x1F4285F4)   // Light blue highlight

    // Piano range constants
    val minFreq = 27.5f  // A0
    val maxFreq = 4186f  // C8
    val minMidi = freqToMidi(minFreq)  // For grid lines only
    val maxMidi = freqToMidi(maxFreq)  // For grid lines only
    val midiRange = maxMidi - minMidi + 1

    // Function to convert frequency to Y position
    fun frequencyToY(freq: Float, height: Float): Float {
        val normalizedLogFreq = (log2(freq/minFreq)) / (log2(maxFreq/minFreq))
        return height * (1 - normalizedLogFreq)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val width = size.width
        val height = size.height
        val keyHeight = height / midiRange

        // Draw background grid lines for each note
        for (midi in minMidi..maxMidi) {
            val isWhiteKey = (midi % 12) in listOf(0, 2, 4, 5, 7, 9, 11)
            val freq = 440f * 2f.pow((midi - 69) / 12f)
            val y = frequencyToY(freq, height)

            drawLine(
                color = if (isWhiteKey) gridLineLight else gridLineDark,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(width, y),
                strokeWidth = if (isWhiteKey) 1.5f else 0.75f
            )
        }

        // Draw the current pitch point
        pitch?.let {
            val y = frequencyToY(it, height)

            // Draw highlight area
            drawRect(
                color = highlightColor,
                topLeft = androidx.compose.ui.geometry.Offset(0f, y - 10f),
                size = androidx.compose.ui.geometry.Size(width, 20f)
            )

            // Draw current pitch point
            drawCircle(
                color = currentPitchColor,
                radius = 6f,
                center = androidx.compose.ui.geometry.Offset(width - 10f, y)
            )
        }

        // Draw pitch history line
        if (pitchHistory.isNotEmpty()) {
            val path = Path()
            var firstPoint = true
            val pointSpacing = width / 100f

            pitchHistory.asReversed().forEachIndexed { index, historyPitch ->
                historyPitch?.let {
                    val x = width - (index * pointSpacing)
                    val y = frequencyToY(it, height)

                    if (firstPoint) {
                        path.moveTo(x, y)
                        firstPoint = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }

            drawPath(
                path = path,
                color = pitchLineColor,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewPitchGraph() {
    // Generate some sample pitch data for preview
    val samplePitch = 440f // A4
    val sampleHistory = List(50) { index ->
        // Create a smooth sine wave pattern for the preview
        val frequency = 440f * 2f.pow(sin(index * 0.1f) * 0.5f)
        frequency
    }

    PitchGraph(
        pitch = samplePitch,
        pitchHistory = sampleHistory
    )
}