package app.voqal.com.feature.onboarding.presentation.fullname

import app.voqal.com.core.presentation.util.UiText

sealed interface FullNameEvent {
    data object Navigate : FullNameEvent
    data class ShowSnackbar(val message: UiText) : FullNameEvent
}