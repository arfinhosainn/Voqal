package app.voqal.com.feature.onboarding.presentation.email

sealed interface EmailAction {
    data class OnEmailChange(val email: String) : EmailAction
    data object OnContinueClick : EmailAction
}
