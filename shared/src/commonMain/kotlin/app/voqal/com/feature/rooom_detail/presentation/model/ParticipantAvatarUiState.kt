package app.voqal.com.feature.rooom_detail.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter

@Immutable
data class ParticipantAvatarUiState(
    val id: String,
    val name: String,
    val avatar: Painter?,
    val countryFlag: Painter?,
    val micState: MicState,
    val isSpeaking: Boolean
)
