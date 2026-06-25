package app.voqal.com.feature.onboarding.presentation.email

data class EmailState(
    val email: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean
        get() = email.trim().let { value ->
            value.contains("@") && value.substringAfter("@").contains(".")
        }
}
