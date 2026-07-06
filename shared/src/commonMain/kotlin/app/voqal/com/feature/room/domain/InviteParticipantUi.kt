package app.voqal.com.feature.room.domain

import androidx.compose.ui.graphics.painter.Painter

data class InviteParticipantUi(
    val id: String,
    val name: String,
    val avatar: Painter? = null,
    val isOnline: Boolean = true,
    val countryCode: String? = null
)
