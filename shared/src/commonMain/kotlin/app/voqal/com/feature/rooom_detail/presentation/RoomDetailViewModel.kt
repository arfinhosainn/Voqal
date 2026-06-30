package app.voqal.com.feature.rooom_detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.voqal.com.core.domain.onFailure
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingRoute
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.RoomConnectionState
import app.voqal.com.feature.room.domain.toParticipantAvatarUiState
import app.voqal.com.feature.room.domain.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val roomCallDataSource: RoomCallRemoteDataSource,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<OnboardingRoute.RoomDetailRoute>()

    val state: StateFlow<RoomDetailState> = combine(
        roomCallDataSource.connectionState,
        roomCallDataSource.roomInfo,
        roomCallDataSource.participants,
        roomCallDataSource.isMicrophoneEnabled
    ) { connection, info, participants, micEnabled ->
        RoomDetailState(
            title = info.title.orEmpty(),
            isLoading = connection != RoomConnectionState.CONNECTED,
            isMicrophoneEnabled = micEnabled,
            participants = participants.map { it.toParticipantAvatarUiState() }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomDetailState())

    private val _events = Channel<RoomDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            roomCallDataSource.joinRoom(roomId = route.roomId, asHost = route.asHost)
                .onFailure { error ->
                    _events.send(RoomDetailEvent.ShowError(error.toUiText()))
                }
        }
    }

    fun onAction(action: RoomDetailAction) {
        when (action) {
            RoomDetailAction.OnLeaveClick -> viewModelScope.launch {
                roomCallDataSource.leaveRoom()
                _events.send(RoomDetailEvent.LeaveRoom)
            }
            RoomDetailAction.OnMicClick -> viewModelScope.launch {
                roomCallDataSource.setMicrophoneEnabled(!state.value.isMicrophoneEnabled)
            }
            RoomDetailAction.OnHandClick -> {
                // TODO: raise-hand / request-to-speak — separate Stream capability, not built yet
            }
            RoomDetailAction.OnMoreClick -> {
                // unrelated to call data, leave as-is
            }
        }
    }
}