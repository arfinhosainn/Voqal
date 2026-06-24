package app.voqal.com.feature.onboarding.presentation.fullname

import app.voqal.com.core.designsystem.presentation.util.UiText

data class FullNameState(
    val firstName: String = "",
    val lastName: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
) {
    val isFormValid: Boolean get() = firstName.isNotBlank() && lastName.isNotBlank()
}