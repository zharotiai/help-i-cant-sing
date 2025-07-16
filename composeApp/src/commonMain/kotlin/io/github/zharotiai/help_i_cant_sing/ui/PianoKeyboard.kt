package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun PianoKeyboard(
    currentPitch: Float?,
    modifier: Modifier = Modifier,
    // These parameters must be shared with PitchGraph for alignment
    verticalScrollState: ScrollState,
    totalHeight: Dp,
    dpPerOctave: Dp,
    freqToY: (Float) -> Float
) {
    val highlightColor = MaterialTheme.colorScheme.secondary
    val whiteKeyColor = MaterialTheme.colorScheme.surface
    val blackKeyColor = MaterialTheme.colorScheme.onSurface
    val keyBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val currentMidiNote = currentPitch?.let { freqToMidi(it) }

    Box(
        modifier = modifier.verticalScroll(verticalScrollState)
    ) {
        Canvas(
            modifier = Modifier.height(totalHeight).fillMaxWidth()
        ) {
            val canvasWidth = size.width
            val pixelsPerOctave = with(density) { dpPerOctave.toPx() }
            val semitoneHeight = pixelsPerOctave / 12f
            val allMidiNotes = (21..108)

            // White keys with borders
            allMidiNotes.forEach { midi ->
                val noteName = midiToNoteName(midi)
                if (!noteName.contains("#")) {
                    val freq = midiToFreq(midi)
                    val y = freqToY(freq)
                    val keyTopY = y - (semitoneHeight / 2f)

                    // key fill
                    drawRect(
                        color = if (midi == currentMidiNote) highlightColor else whiteKeyColor,
                        topLeft = Offset(0f, keyTopY),
                        size = Size(canvasWidth, semitoneHeight)
                    )
                    // border for definition
                    drawRect(
                        color = keyBorderColor,
                        topLeft = Offset(0f, keyTopY),
                        size = Size(canvasWidth, semitoneHeight),
                        style = Stroke(width = 1f)
                    )
                }
            }

            // draw all black keys on top
            allMidiNotes.forEach { midi ->
                val noteName = midiToNoteName(midi)
                if (noteName.contains("#")) {
                    val freq = midiToFreq(midi)
                    val y = freqToY(freq)
                    val keyTopY = y - (semitoneHeight / 2f)

                    drawRect(
                        color = if (midi == currentMidiNote) highlightColor else blackKeyColor,
                        topLeft = Offset(0f, keyTopY),
                        size = Size(canvasWidth * 0.65f, semitoneHeight)
                    )
                }
            }

            // draw note labels on white keys only
            allMidiNotes.forEach { midi ->
                val fullNoteName = midiToNoteName(midi)
                val isBlackKey = fullNoteName.contains("#")

                if (!isBlackKey) {
                    val displayName = fullNoteName.filter { !it.isDigit() }
                    val y = freqToY(midiToFreq(midi))

                    val textColor = if (midi == currentMidiNote) {
                        whiteKeyColor
                    } else {
                        blackKeyColor.copy(alpha = 0.4f)
                    }

                    val textStyle = TextStyle(
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val textLayoutResult = textMeasurer.measure(
                        text = displayName,
                        style = textStyle
                    )

                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = (canvasWidth - textLayoutResult.size.width) / 2f,
                            y = y - (textLayoutResult.size.height / 2f)
                        )
                    )
                }
            }
        }
    }
}