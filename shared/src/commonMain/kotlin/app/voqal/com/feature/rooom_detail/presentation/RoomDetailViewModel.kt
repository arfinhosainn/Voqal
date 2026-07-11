package app.voqal.com.feature.rooom_detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.voqal.com.core.data.StoredRoomData
import app.voqal.com.core.data.UserPreferencesDataSource
import app.voqal.com.core.domain.Result
import app.voqal.com.core.domain.onFailure
import app.voqal.com.core.domain.onSuccess
import app.voqal.com.core.permissions.domain.PermissionManager
import app.voqal.com.core.permissions.domain.PermissionType
import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.RoomConnectionState
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import app.voqal.com.feature.room.domain.toParticipantAvatarUiState
import app.voqal.com.feature.room.domain.toUiText
import app.voqal.com.feature.rooom_detail.presentation.model.RoomPresentationState
import app.voqal.com.feature.rooom_detail.presentation.navigation.RoomDetailRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val roomCallDataSource: RoomCallRemoteDataSource,
    private val connectionRepository: StreamRoomConnectionRepository,
    private val roomDiscoveryRepository: RoomDiscoveryRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val permissionManager: PermissionManager,
    private val presentationStore: RoomPresentationStore
) : ViewModel() {

    private val route = savedStateHandle.toRoute<RoomDetailRoute>()
    private val _isEndRoomDialogVisible = MutableStateFlow(false)
    private val _isRaiseHandSheetVisible = MutableStateFlow(false)
    private val _isChatSheetVisible = MutableStateFlow(false)
    private val _selectedProfile = MutableStateFlow<app.voqal.com.feature.rooom_detail.presentation.components.UserProfileUi?>(null)

    val state: StateFlow<RoomDetailState> = combine(
        roomCallDataSource.connectionState,
        roomCallDataSource.roomInfo,
        roomCallDataSource.participants,
        roomCallDataSource.isMicrophoneEnabled,
        roomCallDataSource.isHost
    ) { connection, info, participants, micEnabled, isHost ->
        RoomDetailState(
            roomId = route.roomId,
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
    }.combine(_isChatSheetVisible) { state, showChat ->
        state.copy(isChatVisible = showChat)
    }.combine(_selectedProfile) { state, profile ->
        state.copy(selectedProfile = profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomDetailState(roomId = route.roomId))

    private val _events = Channel<RoomDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        presentationStore.expand(route.roomId)
        viewModelScope.launch {
            if (route.asHost) {
                // Creator path — already connected, joined in RoomViewModel,
                // and DB auto-join trigger already ran on rooms INSERT
                // Just ensure mic permission and persist room data locally
                permissionManager.request(PermissionType.MICROPHONE)
                userPreferencesDataSource.setStoredRoom(
                    StoredRoomData(
                        roomId = route.roomId,
                        wasHost = true,
                        createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
                    )
                )
            } else {
                // Guest path — need to fully connect and join
                val connectionResult = connectionRepository.ensureUserConnected()
                if (connectionResult is Result.Error) {
                    _events.send(RoomDetailEvent.ShowError(connectionResult.error.toUiText()))
                    return@launch
                }

                val permissionResult = permissionManager.request(PermissionType.MICROPHONE)
                if (permissionResult != app.voqal.com.core.permissions.domain.PermissionResult.GRANTED) {
                    _events.send(RoomDetailEvent.ShowError(RoomCallError.MICROPHONE_PERMISSION_DENIED.toUiText()))
                    return@launch
                }

                roomCallDataSource.joinRoom(roomId = route.roomId, asHost = false)
                    .onSuccess {
                        roomDiscoveryRepository.joinRoom(route.roomId)
                        userPreferencesDataSource.setStoredRoom(
                            StoredRoomData(
                                roomId = route.roomId,
                                wasHost = false,
                                createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
                            )
                        )
                    }
                    .onFailure { error ->
                        _events.send(RoomDetailEvent.ShowError(error.toUiText()))
                    }
            }
        }
    }

    fun onAction(action: RoomDetailAction) {
        when (action) {
            RoomDetailAction.OnLeaveClick -> viewModelScope.launch {
                if (state.value.isHost) {
                    _isEndRoomDialogVisible.update { true }
                } else {
                    userPreferencesDataSource.setStoredRoom(null)
                    presentationStore.clear()
                    roomDiscoveryRepository.leaveRoom(route.roomId)
                    roomCallDataSource.leaveRoom()
                    _events.send(RoomDetailEvent.LeaveRoom)
                }
            }
            RoomDetailAction.OnEndClick -> viewModelScope.launch {
                userPreferencesDataSource.setStoredRoom(null)
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
            RoomDetailAction.OnChatClick -> {
                _isChatSheetVisible.update { true }
            }
            RoomDetailAction.OnShowChatSheet -> {
                _isChatSheetVisible.update { true }
            }
            RoomDetailAction.OnDismissChatSheet -> {
                _isChatSheetVisible.update { false }
            }
            is RoomDetailAction.OnParticipantClick -> {
                _selectedProfile.update { action.participant }
            }
            RoomDetailAction.OnDismissProfileSheet -> {
                _selectedProfile.update { null }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            withContext(NonCancellable) {
                leaveCurrentRoom()
            }
        }
    }

    private suspend fun leaveCurrentRoom() {
        try {
            if (roomCallDataSource.isHost.value) {
                roomDiscoveryRepository.deleteRoom(route.roomId)
                roomCallDataSource.endRoom()
            } else {
                roomCallDataSource.leaveRoom()
            }
            roomDiscoveryRepository.leaveRoom(route.roomId)
            presentationStore.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
