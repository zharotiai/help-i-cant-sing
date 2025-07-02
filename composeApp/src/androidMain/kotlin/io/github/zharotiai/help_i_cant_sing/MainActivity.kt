package io.github.zharotiai.help_i_cant_sing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import io.github.zharotiai.help_i_cant_sing.permissions.PermissionManager
import io.github.zharotiai.help_i_cant_sing.audio.record.AudioRecorderViewModel
import io.github.zharotiai.help_i_cant_sing.audio.AndroidAudioRecorder
import io.github.zharotiai.help_i_cant_sing.audio.detect_pitch.YIN
import java.io.File

class MainActivity : ComponentActivity() {

    // Make permissionManager nullable and initialize it in onCreate,
    // then ensure it's accessed safely or after initialization.
    // We'll initialize the launcher AFTER permissionManager is ready.
    private var permissionManager: PermissionManager? = null

    // The launcher is declared here, but we will assign its callback dynamically
    // or ensure permissionManager is ready when the callback fires.
    // For simplicity and directness, we will now initialize it directly within onCreate
    // to capture the `permissionManager` instance.
    private lateinit var requestAudioPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize the PermissionManager instance first.
        permissionManager = PermissionManager()

        // Now that permissionManager is initialized, we can safely initialize the launcher
        // and its callback can directly reference the initialized instance.
        requestAudioPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // Pass the result to PermissionManager's handler
            // Use the safe call operator `?.` since permissionManager is nullable,
            // though at this point in the lifecycle, it should not be null.
            permissionManager?.handlePermissionResult(isGranted)
        }

        // Prepare output file for recording
        val outputFile = File(cacheDir, "recording.3gp")
        val recorder = AndroidAudioRecorder(this)
        val viewModel = AudioRecorderViewModel(recorder, YIN(), 44100)

        setContent {
            App(viewModel, permissionManager)
        }
        // Request audio permission on startup
        permissionManager?.requestAudioPermissionFromActivity(this, requestAudioPermissionLauncher) {}
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // Make sure your App composable is defined elsewhere and can be previewed.
    // This preview function doesn't directly interact with permissions,
    // as previews typically don't have a live Android context.
    // App()
}