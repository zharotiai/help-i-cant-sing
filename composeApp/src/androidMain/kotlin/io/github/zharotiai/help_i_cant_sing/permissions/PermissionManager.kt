package io.github.zharotiai.help_i_cant_sing.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

actual object PermissionManager {
    private const val RECORD_AUDIO_PERMISSION = android.Manifest.permission.RECORD_AUDIO
    const val REQUEST_CODE = 1001

    private var activity: Activity? = null
    private var permissionCallback: ((Boolean) -> Unit)? = null

    actual fun setActivity(activity: Any) {
        this.activity = activity as? Activity
    }

    actual fun isAudioPermissionGranted(): Boolean {
        val act = activity ?: return false
        return ContextCompat.checkSelfPermission(
            act, RECORD_AUDIO_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun shouldShowRationale(): Boolean {
        val act = activity ?: return false
        return ActivityCompat.shouldShowRequestPermissionRationale(act, RECORD_AUDIO_PERMISSION)
    }

    actual fun requestAudioPermission(onResult: (Boolean) -> Unit) {
        val act = activity ?: return
        permissionCallback = onResult
        ActivityCompat.requestPermissions(
            act, arrayOf(RECORD_AUDIO_PERMISSION), REQUEST_CODE
        )
    }

    // Call this from your Activity's onRequestPermissionsResult
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            permissionCallback?.invoke(granted)
            permissionCallback = null
        }
    }
}
