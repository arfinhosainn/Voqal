package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.EmptyResult

interface OnboardingAuthDataSource {
    suspend fun sendEmailOtp(email: String): EmptyResult<OnboardingAuthError>
    suspend fun verifyEmailOtp(email: String, token: String): EmptyResult<OnboardingAuthError>
}

