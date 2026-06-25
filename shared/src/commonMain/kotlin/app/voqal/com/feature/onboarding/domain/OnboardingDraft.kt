package app.voqal.com.feature.onboarding.domain

data class OnboardingDraft(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val profilePhotoUri: String? = null,
    val selectedLanguageId: String? = null,
    val selectedInterestIds: Set<String> = emptySet(),
    val currentStep: Int = 1,
    val lastUpdatedAtMillis: Long = 0L
)
