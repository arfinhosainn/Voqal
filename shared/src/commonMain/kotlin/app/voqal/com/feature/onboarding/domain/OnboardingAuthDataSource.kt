package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.EmptyResult

interface OnboardingAuthDataSource {
    suspend fun signUp(email: String): EmptyResult<OnboardingAuthError>
}

