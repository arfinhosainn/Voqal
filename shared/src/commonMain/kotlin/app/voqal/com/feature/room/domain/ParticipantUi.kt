package app.voqal.com.feature.room.domain

import androidx.compose.runtime.Immutable

@Immutable
data class ParticipantUi(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val countryCode: String? = null
)
