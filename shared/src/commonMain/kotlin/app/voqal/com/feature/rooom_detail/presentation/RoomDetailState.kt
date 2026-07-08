package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState
import app.voqal.com.feature.rooom_detail.presentation.model.RoomPresentationState

data class RoomDetailState(
    val roomId: String = "",
    val title: String = "",
    val isLoading: Boolean = true,
    val isFailed: Boolean = false,
    val isHost: Boolean = false,
    val isMicrophoneEnabled: Boolean = false,
    val participants: List<ParticipantAvatarUiState> = emptyList(),
    val presentationState: RoomPresentationState = RoomPresentationState.Expanded,
    val isEndRoomDialogVisible: Boolean = false,
    val isRaiseHandSheetVisible: Boolean = false,
    val isChatVisible: Boolean = false
)
