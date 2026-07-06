package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.core.presentation.util.UiText

sealed interface RoomDetailEvent {
    data object LeaveRoom : RoomDetailEvent
    data class ShowError(val error: UiText) : RoomDetailEvent
    data class ShowSnackbar(val message: UiText) : RoomDetailEvent
}
