package app.voqal.com.core.permissions.domain

interface PermissionManager {
    suspend fun request(type: PermissionType): PermissionResult
    suspend fun isGranted(type: PermissionType): Boolean
    fun openSettings()
}
