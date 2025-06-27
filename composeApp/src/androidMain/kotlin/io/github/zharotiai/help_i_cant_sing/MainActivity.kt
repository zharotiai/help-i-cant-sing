package io.github.zharotiai.help_i_cant_sing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.zharotiai.help_i_cant_sing.permissions.PermissionManager

import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result here
        PermissionManager.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        PermissionManager.setActivity(this, requestAudioPermissionLauncher)

        setContent {
            App()
        }
        PermissionManager.requestAudioPermission()
    }
}



@Preview
@Composable
fun AppAndroidPreview() {
    App()
}