package app.voqal.com.feature.permission.presentation

import app.voqal.com.core.permissions.domain.PermissionType

data class PermissionScreenState(
    val emoji: String,
    val title: String,
    val description: String,
    val permissionType: PermissionType,
    val isRequesting: Boolean = false
)
