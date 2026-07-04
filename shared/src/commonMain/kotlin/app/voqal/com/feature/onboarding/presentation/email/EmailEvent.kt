package app.voqal.com.feature.onboarding.presentation.email

sealed interface EmailEvent {
    data class NavigateToNext(val isNewUser: Boolean) : EmailEvent
    data class ShowSnackbar(val message: String) : EmailEvent
}
