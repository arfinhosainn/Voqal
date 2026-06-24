package app.voqal.com.feature.onboarding.presentation.interest

sealed interface ChooseInterestsAction {
    data class OnInterestToggle(val interestId: String) : ChooseInterestsAction
    data object OnContinueClick : ChooseInterestsAction
}