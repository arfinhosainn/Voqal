package app.voqal.com.feature.onboarding.presentation.username

data class UsernameState(
    val username: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean = username.isNotBlank() && username.trim().length >= 3
}