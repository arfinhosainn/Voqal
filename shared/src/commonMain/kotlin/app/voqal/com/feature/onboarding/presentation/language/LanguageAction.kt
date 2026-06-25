package app.voqal.com.feature.onboarding.presentation.language

sealed interface LanguageAction {
    data class OnLanguageSelect(val language: LanguageUi) : LanguageAction
    data class OnSearchQueryChange(val query: String) : LanguageAction
    data object OnContinueClick : LanguageAction
}
