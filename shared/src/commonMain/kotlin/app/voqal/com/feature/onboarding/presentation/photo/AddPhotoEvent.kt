package app.voqal.com.feature.onboarding.presentation.photo

import app.voqal.com.core.designsystem.presentation.util.UiText

sealed interface AddPhotoEvent {
    data object NavigateToNext : AddPhotoEvent
    data class ShowSnackbar(val message: UiText) : AddPhotoEvent
}