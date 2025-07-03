package io.github.zharotiai.help_i_cant_sing.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun RecordButton(
    onRecord: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    isRecording: Boolean,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isRecording) 0.85f else 1f)
    val colorScheme = MaterialTheme.colorScheme

    // Animate the buttons sliding in/out
    AnimatedVisibility(
        visible = isRecording,
        enter = slideInHorizontally() + fadeIn(),
        exit = slideOutHorizontally() + fadeOut(),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Pause/Play Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .border(2.dp, colorScheme.onSurface, CircleShape)
                    .background(if (isPaused) colorScheme.surface else colorScheme.primary)
                    .clickable(onClick = onPause),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    tint = if (isPaused) colorScheme.primary else colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Stop Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .border(2.dp, colorScheme.onSurface, CircleShape)
                    .background(colorScheme.tertiary)
                    .clickable(onClick = onStop),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Recording",
                    tint = colorScheme.onTertiary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    // Main record button
    AnimatedVisibility(
        visible = !isRecording,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, colorScheme.onSurface, CircleShape)
                .clickable(onClick = onRecord),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FiberManualRecord,
                contentDescription = "Start Recording",
                tint = colorScheme.error,    // use error color for record icon (red)
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview
@Composable
private fun RecordButtonPreview() {
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            RecordButton(
                onRecord = {
                    isRecording = true
                    isPaused = false
                },
                onPause = { isPaused = !isPaused },
                onStop = {
                    isRecording = false
                    isPaused = false
                },
                isRecording = isRecording,
                isPaused = isPaused
            )
        }
    }
}
