package app.voqal.com.core.permissions.data

import app.voqal.com.core.permissions.domain.PermissionManager
import app.voqal.com.core.permissions.domain.PermissionResult
import app.voqal.com.core.permissions.domain.PermissionType
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.location.LOCATION
import dev.icerock.moko.permissions.microphone.RECORD_AUDIO
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION

class PermissionManagerImpl(
    private val permissionsController: PermissionsController
) : PermissionManager {

    override suspend fun request(type: PermissionType): PermissionResult {
        return try {
            permissionsController.providePermission(type.toMokoPermission())
            PermissionResult.GRANTED
        } catch (e: Exception) {
            val state = permissionsController.getPermissionState(type.toMokoPermission())
            state.toPermissionResult()
        }
    }

    override suspend fun isGranted(type: PermissionType): Boolean {
        return permissionsController.isPermissionGranted(type.toMokoPermission())
    }

    override fun openSettings() {
        permissionsController.openAppSettings()
    }

    private fun PermissionType.toMokoPermission(): Permission = when (this) {
        PermissionType.LOCATION -> Permission.LOCATION
        PermissionType.MICROPHONE -> Permission.RECORD_AUDIO
        PermissionType.NOTIFICATION -> Permission.REMOTE_NOTIFICATION
        PermissionType.CAMERA -> Permission.CAMERA
    }

    private fun PermissionState.toPermissionResult(): PermissionResult = when (this) {
        PermissionState.Granted -> PermissionResult.GRANTED
        PermissionState.DeniedAlways -> PermissionResult.PERMANENTLY_DENIED
        PermissionState.Denied -> PermissionResult.DENIED
        PermissionState.NotDetermined -> PermissionResult.NOT_DETERMINED
        else -> PermissionResult.DENIED
    }
}
