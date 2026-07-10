package app.voqal.com.feature.room.domain

import app.voqal.com.core.domain.Error
import app.voqal.com.core.presentation.util.UiText
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.error_already_in_room
import voqal.shared.generated.resources.error_join_failed
import voqal.shared.generated.resources.error_mic_permission_denied
import voqal.shared.generated.resources.error_not_connected
import voqal.shared.generated.resources.error_unknown


enum class RoomCallError : Error {
    MICROPHONE_PERMISSION_DENIED,
    NOT_CONNECTED,
    ALREADY_IN_ROOM,
    JOIN_FAILED,
    UNKNOWN
}

fun RoomCallError.toUiText(): UiText = when (this) {
    RoomCallError.MICROPHONE_PERMISSION_DENIED -> UiText.Resource(Res.string.error_mic_permission_denied)
    RoomCallError.NOT_CONNECTED -> UiText.Resource(Res.string.error_not_connected)
    RoomCallError.ALREADY_IN_ROOM -> UiText.Resource(Res.string.error_already_in_room)
    RoomCallError.JOIN_FAILED -> UiText.Resource(Res.string.error_join_failed)
    RoomCallError.UNKNOWN -> UiText.Resource(Res.string.error_unknown)
}