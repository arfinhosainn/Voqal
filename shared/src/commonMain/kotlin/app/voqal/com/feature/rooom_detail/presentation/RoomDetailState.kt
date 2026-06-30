package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState

data class RoomDetailState(
    val title: String = "",
    val isLoading: Boolean = true,
    val isFailed: Boolean = false,
    val isMicrophoneEnabled: Boolean = false,
    val participants: List<ParticipantAvatarUiState> = emptyList()
)