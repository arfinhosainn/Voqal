package app.voqal.com.feature.rooom_detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.voqal.com.core.data.UserPreferencesDataSource
import app.voqal.com.core.domain.onFailure
import app.voqal.com.core.domain.onSuccess
import app.voqal.com.core.permissions.domain.PermissionManager
import app.voqal.com.core.permissions.domain.PermissionType
import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingRoute
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.RoomConnectionState
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import app.voqal.com.feature.room.domain.toParticipantAvatarUiState
import app.voqal.com.feature.room.domain.toUiText
import app.voqal.com.feature.rooom_detail.presentation.model.RoomPresentationState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoomDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val roomCallDataSource: RoomCallRemoteDataSource,
    private val connectionRepository: StreamRoomConnectionRepository,
    private val roomDiscoveryRepository: RoomDiscoveryRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val permissionManager: PermissionManager,
    private val presentationStore: RoomPresentationStore
) : ViewModel() {

    private val route = savedStateHandle.toRoute<OnboardingRoute.RoomDetailRoute>()
    private val _isEndRoomDialogVisible = MutableStateFlow(false)
    private val _isRaiseHandSheetVisible = MutableStateFlow(false)

    val state: StateFlow<RoomDetailState> = combine(
        roomCallDataSource.connectionState,
        roomCallDataSource.roomInfo,
        roomCallDataSource.participants,
        roomCallDataSource.isMicrophoneEnabled,
        roomCallDataSource.isHost
    ) { connection, info, participants, micEnabled, isHost ->
        RoomDetailState(
            title = info.title.orEmpty(),
            isLoading = connection == RoomConnectionState.CONNECTING || connection == RoomConnectionState.RECONNECTING,
            isFailed = connection == RoomConnectionState.FAILED,
            isHost = isHost,
            isMicrophoneEnabled = micEnabled,
            participants = participants.map { it.toParticipantAvatarUiState() }
        )
    }.combine(presentationStore.presentationState) { state, presentation ->
        state.copy(presentationState = presentation)
    }.combine(_isEndRoomDialogVisible) { state, showDialog ->
        state.copy(isEndRoomDialogVisible = showDialog)
    }.combine(_isRaiseHandSheetVisible) { state, showSheet ->
        state.copy(isRaiseHandSheetVisible = showSheet)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomDetailState())

    private val _events = Channel<RoomDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        presentationStore.expand(route.roomId)
        viewModelScope.launch {
            // 1. Ensure user is connected to Stream
            val connectionResult = connectionRepository.ensureUserConnected()
            if (connectionResult is app.voqal.com.core.domain.Result.Failure) {
                _events.send(RoomDetailEvent.ShowError(connectionResult.error.toUiText()))
                return@launch
            }

            // 2. Ensure Microphone Permission is granted
            val permissionResult = permissionManager.request(PermissionType.MICROPHONE)
            if (permissionResult != app.voqal.com.core.permissions.domain.PermissionResult.GRANTED) {
                _events.send(RoomDetailEvent.ShowError(RoomCallError.MICROPHONE_PERMISSION_DENIED.toUiText()))
                return@launch
            }

            // 3. Join the room
            roomCallDataSource.joinRoom(roomId = route.roomId, asHost = route.asHost)
                .onSuccess {
                    roomDiscoveryRepository.joinRoom(route.roomId)
                }
                .onFailure { error ->
                    _events.send(RoomDetailEvent.ShowError(error.toUiText()))
                }
        }
    }

    fun onAction(action: RoomDetailAction) {
        when (action) {
            RoomDetailAction.OnLeaveClick -> viewModelScope.launch {
                if (state.value.isHost) {
                    _isEndRoomDialogVisible.update { true }
                } else {
                    presentationStore.clear()
                    roomDiscoveryRepository.leaveRoom(route.roomId)
                    roomCallDataSource.leaveRoom()
                    _events.send(RoomDetailEvent.LeaveRoom)
                }
            }
            RoomDetailAction.OnEndClick -> viewModelScope.launch {
                presentationStore.clear()
                // Host ending the room always deletes the card
                roomDiscoveryRepository.leaveRoom(route.roomId)
                roomDiscoveryRepository.deleteRoom(route.roomId)
                try {
                    roomCallDataSource.endRoom()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                _events.send(RoomDetailEvent.LeaveRoom)
            }
            RoomDetailAction.OnMicClick -> viewModelScope.launch {
                val result = permissionManager.request(PermissionType.MICROPHONE)
                if (result == app.voqal.com.core.permissions.domain.PermissionResult.GRANTED) {
                    roomCallDataSource.setMicrophoneEnabled(!state.value.isMicrophoneEnabled)
                } else {
                    _events.send(RoomDetailEvent.ShowError(RoomCallError.MICROPHONE_PERMISSION_DENIED.toUiText()))
                }
            }
            RoomDetailAction.OnHandClick -> viewModelScope.launch {
                val mySessionId = connectionRepository.currentUserId // Assuming this matches session ID or we can find it in participants
                val isAlreadyRaised = state.value.participants.find { it.id == mySessionId }?.isHandRaised ?: false
                
                if (isAlreadyRaised) {
                    roomCallDataSource.lowerHand()
                } else if (!userPreferencesDataSource.hasSeenRaiseHandEducation()) {
                    onAction(RoomDetailAction.OnShowRaiseHandSheet)
                } else {
                    roomCallDataSource.raiseHand().onSuccess {
                        _events.send(RoomDetailEvent.ShowSnackbar(UiText.DynamicString("Hand raised!")))
                    }
                }
            }
            RoomDetailAction.OnMinimizeClick -> {
                presentationStore.minimize()
            }
            RoomDetailAction.OnExpandClick -> {
                presentationStore.expand(route.roomId)
            }
            RoomDetailAction.OnShowEndRoomDialog -> {
                _isEndRoomDialogVisible.update { true }
            }
            RoomDetailAction.OnDismissEndRoomDialog -> {
                _isEndRoomDialogVisible.update { false }
            }
            RoomDetailAction.OnShowRaiseHandSheet -> {
                _isRaiseHandSheetVisible.update { true }
            }
            RoomDetailAction.OnDismissRaiseHandSheet -> {
                _isRaiseHandSheetVisible.update { false }
            }
            RoomDetailAction.OnConfirmRaiseHand -> viewModelScope.launch {
                roomCallDataSource.raiseHand().onSuccess {
                    userPreferencesDataSource.setHasSeenRaiseHandEducation(true)
                    _isRaiseHandSheetVisible.update { false }
                    _events.send(RoomDetailEvent.ShowSnackbar(UiText.DynamicString("Hand raised!")))
                }.onFailure {
                    _isRaiseHandSheetVisible.update { false }
                }
            }
        }
    }
}
