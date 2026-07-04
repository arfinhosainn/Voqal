package app.voqal.com.feature.onboarding.presentation.password

sealed interface PasswordEvent {
    data object NavigateToNext : PasswordEvent
    data class ShowSnackbar(val message: String) : PasswordEvent
}
