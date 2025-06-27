package io.github.zharotiai.help_i_cant_sing.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher


object PermissionManager {
    var handler: PermissionHandler? = null

    fun isAudioPermissionGranted() = handler?.isAudioPermissionGranted() ?: false
    fun shouldShowRationale() = handler?.shouldShowRationale() ?: false
    fun requestAudioPermission(onResult: (Boolean) -> Unit = {}) =
        handler?.requestAudioPermission(onResult)
}


