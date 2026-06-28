package app.voqal.com.feature.rooom_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.feature.rooom_detail.presentation.model.MicState
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoomDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(RoomDetailState(
        title = "3 Minute News",
        participants = listOf(
            ParticipantAvatarUiState(
                id = "1",
                name = "Lena Marsh",
                avatar = null,
                countryFlag = null,
                micState = MicState.ON,
                isSpeaking = true
            ),
            ParticipantAvatarUiState(
                id = "2",
                name = "Minerva Spencer",
                avatar = null,
                countryFlag = null,
                micState = MicState.OFF,
                isSpeaking = false
            ),
            ParticipantAvatarUiState(
                id = "3",
                name = "John Carter",
                avatar = null,
                countryFlag = null,
                micState = MicState.MUTED,
                isSpeaking = false
            ),
            ParticipantAvatarUiState(
                id = "4",
                name = "Jon Daniels",
                avatar = null,
                countryFlag = null,
                micState = MicState.ON,
                isSpeaking = false
            ),
            ParticipantAvatarUiState(
                id = "5",
                name = "Della Guerrero",
                avatar = null,
                countryFlag = null,
                micState = MicState.OFF,
                isSpeaking = false
            )
        )
    ))
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
