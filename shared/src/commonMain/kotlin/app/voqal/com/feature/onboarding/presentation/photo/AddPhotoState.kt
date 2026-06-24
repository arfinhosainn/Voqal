package app.voqal.com.feature.onboarding.presentation.photo

import app.voqal.com.core.designsystem.presentation.util.UiText

data class AddPhotoState(
    val profilePhotoUri: String? = null, // Holds the path/URI of the selected image
    val isSubmitting: Boolean = false,
    val error: UiText? = null
)