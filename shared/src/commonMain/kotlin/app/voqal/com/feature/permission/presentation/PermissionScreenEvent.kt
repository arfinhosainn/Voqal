package app.voqal.com.feature.permission.presentation

import app.voqal.com.core.permissions.domain.PermissionResult

sealed interface PermissionScreenEvent {
    data class PermissionHandled(val result: PermissionResult) : PermissionScreenEvent
}
