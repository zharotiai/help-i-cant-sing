package io.github.zharotiai.help_i_cant_sing.permissions

interface PermissionHandler {
    fun isAudioPermissionGranted(): Boolean
    fun shouldShowRationale(): Boolean
    fun requestAudioPermission(onResult: (Boolean) -> Unit = {})
}
