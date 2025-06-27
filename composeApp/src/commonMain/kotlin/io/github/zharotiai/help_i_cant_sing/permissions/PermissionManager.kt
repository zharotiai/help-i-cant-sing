package io.github.zharotiai.help_i_cant_sing.permissions

expect object PermissionManager {
    fun isAudioPermissionGranted(): Boolean
    fun shouldShowRationale(): Boolean
    fun requestAudioPermission(onResult: (Boolean) -> Unit)
    fun setActivity(activity: Any)
}