package app.voqal.com.feature.room.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantPreviewDto(
    val id: String,
    val name: String,
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null
)
