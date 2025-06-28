package io.github.zharotiai.help_i_cant_sing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.result.contract.ActivityResultContracts
import io.github.zharotiai.help_i_cant_sing.permissions.PermissionManager
import io.github.zharotiai.help_i_cant_sing.permissions.AndroidPermissionHandler
import io.github.zharotiai.help_i_cant_sing.audio.AndroidAudioRecorder
import io.github.zharotiai.help_i_cant_sing.audio.AudioRecorderViewModel
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var permissionHandler: AndroidPermissionHandler

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Call the handler's result method
        permissionHandler.onRequestPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Create and set the handler
        permissionHandler = AndroidPermissionHandler(this, requestAudioPermissionLauncher)
        PermissionManager.handler = permissionHandler

        // Prepare output file for recording
        val outputFile = File(cacheDir, "recording.3gp")
        val recorder = AndroidAudioRecorder(outputFile)
        val viewModel = AudioRecorderViewModel(recorder)

        setContent {
            App(viewModel)
        }
        PermissionManager.requestAudioPermission()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}