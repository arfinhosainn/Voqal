package app.voqal.com.feature.onboarding.presentation.password

import app.voqal.com.feature.onboarding.domain.validation.ValidatePassword

data class PasswordState(
    val password: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean
        get() = ValidatePassword().validate(password)
}
