package io.github.zharotiai.help_i_cant_sing.permissions

actual object PermissionManager {
    actual fun isAudioPermissionGranted(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun shouldShowRationale(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun requestAudioPermission(onResult: (Boolean) -> Unit) {
    }

    actual fun setActivity(activity: Any) {
    }
}