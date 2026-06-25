package app.voqal.com.feature.onboarding.presentation.email

sealed interface EmailEvent {
    data object NavigateToNext : EmailEvent
    data class ShowSnackbar(val message: String) : EmailEvent
}
