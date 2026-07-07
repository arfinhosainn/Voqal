package app.voqal.com.feature.rooom_detail.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter

@Immutable
data class ParticipantAvatarUiState(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val countryCode: String?,
    val micState: MicState,
    val isSpeaking: Boolean,
    val isHandRaised: Boolean = false,
    val handRaisedTimestamp: Long = 0L
)
