package app.voqal.com.feature.onboarding.data

import app.voqal.com.core.data.SupabaseConfig
import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.data.dto.CompleteOnboardingParams
import app.voqal.com.feature.onboarding.data.dto.ProfileIdDto
import app.voqal.com.feature.onboarding.data.dto.ProfileUpdateDto
import app.voqal.com.feature.onboarding.data.dto.ProfileUpsertDto
import app.voqal.com.feature.onboarding.domain.OnboardingProfileDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingProfileError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.ContentType

private const val ProfilesTable = "profiles"
private const val AvatarsBucket = "avatars"

class SupabaseOnboardingProfileDataSource(
    private val supabaseClient: SupabaseClient,
    private val supabaseConfig: SupabaseConfig
) : OnboardingProfileDataSource {

    override suspend fun getOnboardingStep(): Result<Int?, OnboardingProfileError> {
        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.Success(null)

            val profile = supabaseClient.postgrest.from(ProfilesTable)
                .select(columns = Columns.list("onboarding_step")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<ProfileUpdateDto>()

            Result.Success(profile?.onboardingStep)
        } catch (throwable: Throwable) {
            Result.Failure(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun ensureProfileExists(): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest.from(ProfilesTable).upsert(
                ProfileUpsertDto(
                    id = userId,
                    onboardingStep = 2
                )
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Failure(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateFullName(
        firstName: String,
        lastName: String
    ): EmptyResult<OnboardingProfileError> {
        return updateProfile(
            ProfileUpdateDto(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                onboardingStep = 3
            )
        )
    }

    override suspend fun isUsernameAvailable(
        username: String
    ): Result<Boolean, OnboardingProfileError> {
        if (!supabaseConfig.isConfigured) {
            return Result.Failure(OnboardingProfileError.NotConfigured)
        }

        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
            val normalizedUsername = username.trim().lowercase()
            val existingProfiles = supabaseClient.postgrest
                .from(ProfilesTable)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("username", normalizedUsername)
                    }
                }
                .decodeList<ProfileIdDto>()

            Result.Success(existingProfiles.none { it.id != userId })
        } catch (throwable: Throwable) {
            Result.Failure(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateUsername(username: String): EmptyResult<OnboardingProfileError> {
        val normalizedUsername = username.trim().lowercase()

        return when (val availability = isUsernameAvailable(normalizedUsername)) {
            is Result.Failure -> availability
            is Result.Success -> {
                if (!availability.data) {
                    Result.Failure(OnboardingProfileError.UsernameTaken)
                } else {
                    updateProfile(
                        ProfileUpdateDto(
                            username = normalizedUsername,
                            onboardingStep = 4
                        )
                    )
                }
            }
        }
    }

    override suspend fun uploadAvatar(photoBytes: ByteArray): EmptyResult<OnboardingProfileError> {
        if (photoBytes.isEmpty()) {
            return updateProfile(ProfileUpdateDto(onboardingStep = 5))
        }

        return try {
            val userId = requireUserId()
            val (extension, contentType) = photoBytes.detectImageType()
            val avatarPath = "$userId/avatar.$extension"

            supabaseClient.storage
                .from(AvatarsBucket)
                .upload(avatarPath, photoBytes) {
                    upsert = true
                    this.contentType = contentType
                }

            updateProfile(
                ProfileUpdateDto(
                    avatarPath = avatarPath,
                    onboardingStep = 5
                )
            )
        } catch (throwable: Throwable) {
            Result.Failure(throwable.toOnboardingProfileError())
        }
    }

    override suspend fun updateLanguage(languageCode: String): EmptyResult<OnboardingProfileError> {
        return updateProfile(
            ProfileUpdateDto(
                primaryLanguageCode = languageCode,
                onboardingStep = 6
            )
        )
    }

    override suspend fun completeOnboarding(
        interestIds: Set<String>
    ): EmptyResult<OnboardingProfileError> {
        return try {
            requireUserId()
            supabaseClient.postgrest.rpc(
                function = "complete_onboarding",
                parameters = CompleteOnboardingParams(
                    interestIds = interestIds.toList()
                )
            )
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Failure(throwable.toOnboardingProfileError())
        }
    }

    private suspend fun updateProfile(
        update: ProfileUpdateDto
    ): EmptyResult<OnboardingProfileError> {
        return try {
            val userId = requireUserId()
            supabaseClient.postgrest
                .from(ProfilesTable)
                .update(update) {
                    filter {
                        eq("id", userId)
                    }
                }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Failure(throwable.toOnboardingProfileError())
        }
    }

    private fun requireUserId(): String {
        if (!supabaseConfig.isConfigured) {
            throw OnboardingProfileNotConfiguredException()
        }

        return supabaseClient.auth.currentUserOrNull()?.id
            ?: throw OnboardingProfileNotAuthenticatedException()
    }

    private fun Throwable.toOnboardingProfileError(): OnboardingProfileError {
        return when (this) {
            is OnboardingProfileNotConfiguredException -> OnboardingProfileError.NotConfigured
            is OnboardingProfileNotAuthenticatedException -> OnboardingProfileError.NotAuthenticated
            is HttpRequestException,
            is HttpRequestTimeoutException -> OnboardingProfileError.Network
            is RestException -> when {
                isUsernameConflict() -> OnboardingProfileError.UsernameTaken
                else -> OnboardingProfileError.Unknown
            }
            else -> OnboardingProfileError.Unknown
        }
    }

    private fun RestException.isUsernameConflict(): Boolean {
        return statusCode == 409 ||
            message?.contains("profiles_username_key", ignoreCase = true) == true ||
            message?.contains("duplicate key", ignoreCase = true) == true
    }

    private fun ByteArray.detectImageType(): Pair<String, ContentType> {
        return when {
            size >= 3 &&
                this[0] == 0xFF.toByte() &&
                this[1] == 0xD8.toByte() &&
                this[2] == 0xFF.toByte() -> {
                "jpg" to ContentType.Image.JPEG
            }
            size >= 8 &&
                this[0] == 0x89.toByte() &&
                this[1] == 0x50.toByte() &&
                this[2] == 0x4E.toByte() &&
                this[3] == 0x47.toByte() -> {
                "png" to ContentType.Image.PNG
            }
            size >= 12 &&
                this[0] == 0x52.toByte() &&
                this[1] == 0x49.toByte() &&
                this[2] == 0x46.toByte() &&
                this[3] == 0x46.toByte() -> {
                "webp" to ContentType("image", "webp")
            }
            else -> "jpg" to ContentType.Image.JPEG
        }
    }
}

private class OnboardingProfileNotConfiguredException : Exception()
private class OnboardingProfileNotAuthenticatedException : Exception()
