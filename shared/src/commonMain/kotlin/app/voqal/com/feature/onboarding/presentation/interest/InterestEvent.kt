package app.voqal.com.feature.onboarding.presentation.interest

sealed interface ChooseInterestsEvent {
    data class NavigateToNext(val selectedIds: List<String>) : ChooseInterestsEvent
    data class ShowSnackbar(val message: String) : ChooseInterestsEvent
}