package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result

interface OnboardingAuthDataSource {
    suspend fun checkEmailExists(email: String): Result<Boolean, OnboardingAuthError>
    suspend fun signUp(email: String, password: String): EmptyResult<OnboardingAuthError>
    suspend fun signIn(email: String, password: String): EmptyResult<OnboardingAuthError>
}

