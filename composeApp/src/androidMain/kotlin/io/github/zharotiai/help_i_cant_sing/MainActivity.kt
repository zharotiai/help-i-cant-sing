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

    private var permissionManager: PermissionManager? = null

    private lateinit var requestAudioPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager()

        requestAudioPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
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
