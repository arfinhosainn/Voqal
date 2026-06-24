package app.voqal.com.feature.onboarding.presentation.language

sealed interface LanguageEvent {
    data class NavigateToNext(val chosenLanguage: LanguageUi) : LanguageEvent
}