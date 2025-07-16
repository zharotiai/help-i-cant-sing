package io.github.zharotiai.help_i_cant_sing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import io.github.zharotiai.help_i_cant_sing.ui.MyPitchScreen
import io.github.zharotiai.help_i_cant_sing.ui.theme.AppTheme

@Composable
fun App(viewModel: AudioRecorderViewModel?, permissionManager: PermissionManager?) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            if (permissionManager != null) {
                LaunchedEffect(Unit) {
                    permissionManager.requestAudioPermission{}
                }
            }

            if (viewModel != null) {
                MyPitchScreen(viewModel)
            } else {
                Text("No ViewModel provided.")
            }
        }
    }
}
