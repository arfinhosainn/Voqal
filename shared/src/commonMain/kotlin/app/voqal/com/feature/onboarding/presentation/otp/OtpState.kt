package app.voqal.com.feature.onboarding.presentation.otp

data class OtpState(
    val code: List<Int?> = List(6) { null },
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val emailAddress: String = "hello@email.com" // Hoisted out of hardcoding
) {
    val isValid: Boolean = code.all { it != null }
    val codeString: String = code.joinToString("") { it?.toString() ?: "" }
}