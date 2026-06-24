package app.voqal.com.feature.onboarding.presentation.username

sealed interface UsernameAction {
    data class OnUsernameChange(val username: String) : UsernameAction
    object OnContinueClick : UsernameAction
}