// src/androidMain/kotlin/io/github/zharotiai/help_i_cant_sing/permissions/AndroidPermissionHandler.kt
package io.github.zharotiai.help_i_cant_sing.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher

class AndroidPermissionHandler(
    private val activity: Activity,
    private val launcher: ActivityResultLauncher<String>
) : PermissionHandler {
    private var permissionCallback: ((Boolean) -> Unit)? = null
    private val RECORD_AUDIO_PERMISSION = android.Manifest.permission.RECORD_AUDIO

    override fun isAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, RECORD_AUDIO_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun shouldShowRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, RECORD_AUDIO_PERMISSION)
    }

    override fun requestAudioPermission(onResult: (Boolean) -> Unit) {
        permissionCallback = onResult
        launcher.launch(RECORD_AUDIO_PERMISSION)
    }

    fun onRequestPermissionResult(granted: Boolean) {
        permissionCallback?.invoke(granted)
        permissionCallback = null
    }
}