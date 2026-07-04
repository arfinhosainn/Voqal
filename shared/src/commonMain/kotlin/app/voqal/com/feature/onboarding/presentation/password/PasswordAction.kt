package app.voqal.com.feature.onboarding.presentation.password

sealed interface PasswordAction {
    data class OnPasswordChange(val password: String) : PasswordAction
    data object OnContinueClick : PasswordAction
    data object OnForgotPasswordClick : PasswordAction
    data object OnMagicLinkClick : PasswordAction
}
