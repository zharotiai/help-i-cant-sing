package io.github.zharotiai.help_i_cant_sing.permissions

interface PermissionManager {
    fun isAudioPermissionGranted(): Boolean
    fun shouldShowRationale(): Boolean
    fun requestAudioPermission(onResult: (Boolean) -> Unit = {})
}
