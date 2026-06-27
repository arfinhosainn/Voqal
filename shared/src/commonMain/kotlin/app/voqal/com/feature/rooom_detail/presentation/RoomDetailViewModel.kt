package app.voqal.com.feature.rooom_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoomDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(RoomDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<RoomDetailEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: RoomDetailAction) {
        when (action) {
            RoomDetailAction.OnLeaveClick -> {
                viewModelScope.launch {
                    _events.send(RoomDetailEvent.LeaveRoom)
                }
            }
            RoomDetailAction.OnHandClick -> {
                // TODO: Handle hand raise
            }
            RoomDetailAction.OnMicClick -> {
                // TODO: Handle mic toggle
            }
            RoomDetailAction.OnMoreClick -> {
                // TODO: Handle more options
            }
        }
    }
}
