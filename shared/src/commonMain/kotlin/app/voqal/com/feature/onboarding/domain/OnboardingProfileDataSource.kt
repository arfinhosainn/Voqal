package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result

interface OnboardingProfileDataSource {
    suspend fun ensureProfileExists(): EmptyResult<OnboardingProfileError>
    suspend fun updateFullName(firstName: String, lastName: String): EmptyResult<OnboardingProfileError>
    suspend fun isUsernameAvailable(username: String): Result<Boolean, OnboardingProfileError>
    suspend fun updateUsername(username: String): EmptyResult<OnboardingProfileError>
    suspend fun uploadAvatar(photoBytes: ByteArray): EmptyResult<OnboardingProfileError>
    suspend fun updateLanguage(languageCode: String): EmptyResult<OnboardingProfileError>
    suspend fun completeOnboarding(interestIds: Set<String>): EmptyResult<OnboardingProfileError>
}
