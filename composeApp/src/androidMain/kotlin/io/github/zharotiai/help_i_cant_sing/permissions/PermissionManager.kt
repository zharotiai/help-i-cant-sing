package io.github.zharotiai.help_i_cant_sing.permissions

import android.Manifest // <-- Add this import
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

actual class PermissionManager actual constructor() {

    private var pendingPermissionCallback: ((Boolean) -> Unit)? = null

    fun requestAudioPermissionFromActivity(
        activity: Activity,
        launcher: ActivityResultLauncher<String>,
        onResult: (Boolean) -> Unit
    ) {
        // Use the fully qualified name or ensure Manifest.permission is imported
        val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO

        if (ContextCompat.checkSelfPermission(activity, RECORD_AUDIO_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            onResult(true)
        } else {
            pendingPermissionCallback = onResult
            launcher.launch(RECORD_AUDIO_PERMISSION)
        }
    }

    fun handlePermissionResult(granted: Boolean) {
        pendingPermissionCallback?.invoke(granted)
        pendingPermissionCallback = null
    }

    actual fun isAudioPermissionGranted(): Boolean {
        val appContext: Context = AndroidApplicationContext.get()
        return ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun shouldShowRationale(): Boolean {
        return false
    }

    actual fun requestAudioPermission(onResult: (Boolean) -> Unit) {
        onResult(false)
        println("Warning: On Android, 'requestAudioPermission()' from common cannot directly trigger UI permission prompt. Use 'PermissionManager().requestAudioPermissionFromActivity(activity, launcher, onResult)' from your Android Activity.")
    }
}

object AndroidApplicationContext {
    private var context: Context? = null

    fun set(appContext: Context) {
        context = appContext.applicationContext
    }

    fun get(): Context {
        return context ?: error("Android Application Context has not been set. Call AndroidApplicationContext.set(this) in your Application's onCreate.")
    }
}
