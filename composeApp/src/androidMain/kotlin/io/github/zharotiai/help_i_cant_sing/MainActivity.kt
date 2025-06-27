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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
        PermissionManager.requestAudioPermission { granted ->
            // Handle permission result if needed
        }
    }
}

@Composable
fun App() {
    MaterialTheme {
        Surface {
            Text("Hello, Help I Can't Sing!")
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}