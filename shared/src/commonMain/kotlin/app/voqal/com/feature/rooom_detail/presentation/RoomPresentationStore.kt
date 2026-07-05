package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.feature.rooom_detail.presentation.model.RoomPresentationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RoomPresentationStore {
    private val _presentationState = MutableStateFlow(RoomPresentationState.Expanded)
    val presentationState = _presentationState.asStateFlow()

    private val _activeRoomId = MutableStateFlow<String?>(null)
    val activeRoomId = _activeRoomId.asStateFlow()

    fun minimize() {
        _presentationState.value = RoomPresentationState.Minimized
    }

    fun expand(roomId: String) {
        _activeRoomId.value = roomId
        _presentationState.value = RoomPresentationState.Expanded
    }

    fun clear() {
        _activeRoomId.value = null
        _presentationState.value = RoomPresentationState.Expanded
    }
}
