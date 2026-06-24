package app.voqal.com.feature.onboarding.presentation.fullname

sealed interface FullNameAction {
    data class OnFirstNameChange(val value: String) : FullNameAction
    data class OnLastNameChange(val value: String) : FullNameAction
    data object OnContinueClick : FullNameAction
}