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
import io.github.zharotiai.help_i_cant_sing.ui.theme.AppTheme


private const val RECORD_COLOUR = 0xFFCC4C4C

@Composable
fun RecordButton(
    onRecord: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    isRecording: Boolean,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // Animation values for splitting effect
    val buttonsOffset by animateFloatAsState(
        targetValue = if (isRecording) 32f else 0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessLow
        )
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isRecording) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Control buttons (Pause/Play and Stop)
        AnimatedVisibility(
            visible = isRecording,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = 0.dp, y = 0.dp)
            ) {
                // Pause/Play Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(x = (-buttonsOffset).dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .border(2.dp, colorScheme.onSurface, CircleShape)
                        .background(if (isPaused) colorScheme.surface else colorScheme.primary)
                        .clickable(onClick = onPause),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = if (isPaused) colorScheme.primary else colorScheme.surface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Stop Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(x = buttonsOffset.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .border(2.dp, colorScheme.onSurface, CircleShape)
                        .background(colorScheme.tertiary)
                        .clickable(onClick = onStop),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Recording",
                        tint = colorScheme.surface,
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
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, colorScheme.onSurface, CircleShape)
                    .background(colorScheme.surface)
                    .clickable(onClick = onRecord),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FiberManualRecord,
                    contentDescription = "Start Recording",
                    tint = Color(RECORD_COLOUR),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecordButtonPreview() {
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    AppTheme {  // Using our custom AppTheme instead of MaterialTheme
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
