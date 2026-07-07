package app.voqal.com.feature.room.domain

import androidx.compose.runtime.Immutable

@Immutable
data class InviteParticipantUi(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val isOnline: Boolean = true,
    val countryCode: String? = null
)
