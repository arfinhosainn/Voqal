package app.voqal.com.feature.onboarding.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpsertDto(
    val id: String,
    val email: String,
    @SerialName("onboarding_step")
    val onboardingStep: Int = 2
)

@Serializable
data class ProfileIdDto(
    val id: String
)

@Serializable
data class ProfileUpdateDto(
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val username: String? = null,
    val email: String? = null,
    @SerialName("primary_language_code")
    val primaryLanguageCode: String? = null,
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("onboarding_step")
    val onboardingStep: Int? = null
)

@Serializable
data class CompleteOnboardingParams(
    @SerialName("p_interest_ids")
    val interestIds: List<String>
)
