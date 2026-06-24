package app.voqal.com.feature.onboarding.presentation.photo

sealed interface AddPhotoAction {
    data class OnPhotoSelected(val uri: String) : AddPhotoAction
    data object OnContinueClick : AddPhotoAction
}