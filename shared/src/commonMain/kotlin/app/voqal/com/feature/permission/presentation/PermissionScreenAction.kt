package app.voqal.com.feature.permission.presentation

sealed interface PermissionScreenAction {
    data object OnRequestClick : PermissionScreenAction
}
