package app.voqal.com.feature.onboarding.presentation.password

sealed interface PasswordEvent {
    data class NavigateToNext(val step: Int?) : PasswordEvent
    data class ShowSnackbar(val message: String) : PasswordEvent
}
