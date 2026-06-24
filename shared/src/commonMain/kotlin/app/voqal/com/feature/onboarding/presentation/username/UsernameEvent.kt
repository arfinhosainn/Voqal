package app.voqal.com.feature.onboarding.presentation.username

sealed interface UsernameEvent {
    object NavigateToNext : UsernameEvent
    data class ShowSnackbar(val message: String) : UsernameEvent
}