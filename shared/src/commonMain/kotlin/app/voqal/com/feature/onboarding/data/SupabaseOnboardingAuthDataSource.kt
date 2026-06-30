package app.voqal.com.feature.onboarding.data

import app.voqal.com.core.data.SupabaseConfig
import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingAuthError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException

class SupabaseOnboardingAuthDataSource(
    private val supabaseClient: SupabaseClient,
    private val supabaseConfig: SupabaseConfig
) : OnboardingAuthDataSource {

    override suspend fun signUp(email: String): EmptyResult<OnboardingAuthError> {
        if (!supabaseConfig.isConfigured) {
            return Result.Failure(OnboardingAuthError.NotConfigured)
        }

        return try {
            // Using a simple signup since email verification is disabled in Supabase
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = "VoqalPassword123!" // Hardcoded for simplified testing flow
            }
            Result.Success(Unit)
        } catch (e: Throwable) {
            // If user already exists, try signing in
            try {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = "VoqalPassword123!"
                }
                Result.Success(Unit)
            } catch (inner: Throwable) {
                Result.Failure(e.toOnboardingAuthError(default = OnboardingAuthError.Unknown))
            }
        }
    }

    private fun Throwable.toOnboardingAuthError(
        default: OnboardingAuthError
    ): OnboardingAuthError {
        return when (this) {
            is HttpRequestException,
            is HttpRequestTimeoutException -> OnboardingAuthError.Network
            is AuthRestException -> when {
                statusCode == 400 -> default
                statusCode == 422 -> OnboardingAuthError.InvalidEmail
                statusCode == 429 -> OnboardingAuthError.TooManyRequests
                else -> OnboardingAuthError.Unknown
            }
            else -> default
        }
    }
}

