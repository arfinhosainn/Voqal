package app.voqal.com.feature.onboarding.presentation

class OnboardingDraftStore {
    var email: String = ""
    var otpCode: List<Int?> = List(6) { null }
    var firstName: String = ""
    var lastName: String = ""
    var username: String = ""
    var profilePhotoBytes: ByteArray? = null
    var selectedLanguageId: String? = null
    var selectedInterestIds: Set<String> = emptySet()

    fun clear() {
        email = ""
        otpCode = List(6) { null }
        firstName = ""
        lastName = ""
        username = ""
        profilePhotoBytes = null
        selectedLanguageId = null
        selectedInterestIds = emptySet()
    }
}
