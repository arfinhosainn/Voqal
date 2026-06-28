package app.voqal.com.feature.room.presentation

import androidx.lifecycle.ViewModel
import app.voqal.com.feature.room.presentation.model.RoomType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RoomState(
    val showRoomTypeSheet: Boolean = false,
    val selectedRoomType: RoomType = RoomType.SOCIAL
)

class RoomViewModel : ViewModel() {
    private val _state = MutableStateFlow(RoomState())
    val state = _state.asStateFlow()

    fun onAction(action: RoomAction) {
        when (action) {
            RoomAction.OnCreateRoomClick -> {
                _state.update { it.copy(showRoomTypeSheet = true) }
            }
            RoomAction.OnDismissSheet -> {
                _state.update { it.copy(showRoomTypeSheet = false) }
            }
            is RoomAction.OnRoomTypeSelected -> {
                _state.update { it.copy(
                    selectedRoomType = action.type
                ) }
            }
            RoomAction.OnStartClick -> {
                // Handle room creation logic here
                _state.update { it.copy(showRoomTypeSheet = false) }
            }
        }
    }
}

sealed interface RoomAction {
    data object OnCreateRoomClick : RoomAction
    data object OnDismissSheet : RoomAction
    data object OnStartClick : RoomAction
    data class OnRoomTypeSelected(val type: RoomType) : RoomAction
}
