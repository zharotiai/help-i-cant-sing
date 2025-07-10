package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

@Composable
fun PitchGraph(
    pitch: Float?,
    pitchHistory: List<Float?>,
    modifier: Modifier = Modifier,
    // --- HOISTED PARAMETERS ---
    // These are now passed in from the parent (MyPitchScreen)
    verticalScrollState: ScrollState,
    totalCanvasHeight: Dp,
    freqToY: (Float) -> Float
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surface
    val dpPerHistoryPoint = 4.dp

    BoxWithConstraints(modifier = modifier) {
        val viewportWidth = this.maxWidth
        val density = LocalDensity.current

        val nowLineFraction = 0.75f
        val rightPadding = viewportWidth * (1f - nowLineFraction)
        val calculatedHistoryWidth = (dpPerHistoryPoint.value * pitchHistory.size).dp
        val canvasWidth = maxOf(viewportWidth, calculatedHistoryWidth + rightPadding)

        // Horizontal scroll state is still managed internally as it's unique to the graph
        val horizontalScrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        // This LaunchedEffect for horizontal scrolling remains here.
        LaunchedEffect(pitchHistory.size) {
            val dpPerHistoryPointPx = with(density) { dpPerHistoryPoint.toPx() }
            val viewportWidthPx = with(density) { viewportWidth.toPx() }
            val lastPointX = (pitchHistory.size - 1).coerceAtLeast(0) * dpPerHistoryPointPx
            val nowLineScreenX = viewportWidthPx * nowLineFraction
            val targetScrollX = (lastPointX - nowLineScreenX).coerceAtLeast(0f)

            coroutineScope.launch {
                horizontalScrollState.animateScrollTo(targetScrollX.roundToInt())
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState) // Uses the passed-in state
                .horizontalScroll(horizontalScrollState)
        ) {
            Canvas(
                modifier = Modifier
                    .width(canvasWidth)
                    .height(totalCanvasHeight) // Uses the passed-in height
                    .background(backgroundColor)
            ) {
                val width = size.width
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

                if (pitchHistory.isNotEmpty()) {
                    val path = Path()
                    val stepX = dpPerHistoryPoint.toPx()
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
                            lastValidFreq = null
                        }
                    }
                    drawPath(
                        path = path,
                        color = lineColor.copy(alpha = 0.7f),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                pitch?.let { currentPitch ->
                    val y = freqToY(currentPitch)
                    val nearestMidi = freqToMidi(currentPitch)
                    val targetPitchFreq = midiToFreq(nearestMidi)
                    val centsOff = 1200 * log2(currentPitch / targetPitchFreq)
                    val toleranceCents = 10.0
                    val color = if (abs(centsOff) <= toleranceCents) Color.Green else Color.Red
                    val currentX = (pitchHistory.size - 1).coerceAtLeast(0) * dpPerHistoryPoint.toPx()

                    drawCircle(
                        color = color,
                        radius = 5.dp.toPx(),
                        center = Offset(currentX, y)
                    )
                }
            }
        }
    }
}