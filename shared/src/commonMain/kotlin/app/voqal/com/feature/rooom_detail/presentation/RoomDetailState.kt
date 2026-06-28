package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState

data class RoomDetailState(
    val roomId: String = "",
    val title: String = "",
    val participants: List<ParticipantAvatarUiState> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
