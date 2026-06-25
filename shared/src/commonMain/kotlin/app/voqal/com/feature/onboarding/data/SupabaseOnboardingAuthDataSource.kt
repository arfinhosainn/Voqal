package app.voqal.com.feature.onboarding.data

import app.voqal.com.core.data.SupabaseConfig
import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingAuthError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException

class SupabaseOnboardingAuthDataSource(
    private val supabaseClient: SupabaseClient,
    private val supabaseConfig: SupabaseConfig
) : OnboardingAuthDataSource {

    override suspend fun sendEmailOtp(email: String): EmptyResult<OnboardingAuthError> {
        if (!supabaseConfig.isConfigured) {
            return Result.Failure(OnboardingAuthError.NotConfigured)
        }

        return try {
            supabaseClient.auth.signInWith(OTP, redirectUrl = null) {
                this.email = email
            }
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Failure(e.toOnboardingAuthError(default = OnboardingAuthError.Unknown))
        }
    }

    override suspend fun verifyEmailOtp(
        email: String,
        token: String
    ): EmptyResult<OnboardingAuthError> {
        if (!supabaseConfig.isConfigured) {
            return Result.Failure(OnboardingAuthError.NotConfigured)
        }

        return try {
            supabaseClient.auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email,
                token = token
            )
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Failure(e.toOnboardingAuthError(default = OnboardingAuthError.InvalidOtp))
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

