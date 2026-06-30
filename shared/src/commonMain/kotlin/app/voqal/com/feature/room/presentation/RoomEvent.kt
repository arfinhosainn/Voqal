package app.voqal.com.feature.room.presentation

import app.voqal.com.core.presentation.util.UiText

sealed interface RoomEvent {
    data class RoomCreated(val roomId: String) : RoomEvent
    data class Error(val error: UiText) : RoomEvent
}
