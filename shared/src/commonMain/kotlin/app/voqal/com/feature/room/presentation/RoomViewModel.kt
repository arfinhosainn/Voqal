package app.voqal.com.feature.room.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.core.utils.UuidUtils
import app.voqal.com.core.domain.onFailure
import app.voqal.com.core.domain.onSuccess
import app.voqal.com.core.presentation.util.UiText
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import app.voqal.com.feature.room.domain.toUiText
import app.voqal.com.feature.room.presentation.model.RoomType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RoomState(
    val showRoomTypeSheet: Boolean = false,
    val selectedRoomType: RoomType = RoomType.SOCIAL,
    val isCreatingRoom: Boolean = false,
    val rooms: List<NewsRoomUi> = emptyList()
)

class RoomViewModel(
    private val roomCallDataSource: RoomCallRemoteDataSource,
    private val connectionRepository: StreamRoomConnectionRepository,
    private val roomDiscoveryRepository: RoomDiscoveryRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(RoomState())
    val state = combine(
        _state,
        roomDiscoveryRepository.getRoomsFlow()
    ) { state, rooms ->
        state.copy(rooms = rooms)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomState())

    private val _events = Channel<RoomEvent>()
    val events = _events.receiveAsFlow()

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
            RoomAction.OnStartClick -> createRoom()
        }
    }

    private fun createRoom() {
        if (_state.value.isCreatingRoom) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isCreatingRoom = true) }

                val connectionResult = connectionRepository.ensureUserConnected()
                if (connectionResult is Result.Error) {
                    _state.update { it.copy(isCreatingRoom = false) }
                    _events.send(RoomEvent.Error(connectionResult.error.toUiText()))
                    return@launch
                }

                val roomId = UuidUtils.randomUuid()
                val roomTitle = _state.value.selectedRoomType.title

                val joinResult = roomCallDataSource.joinRoom(
                    roomId = roomId,
                    asHost = true,
                    title = roomTitle
                )

                joinResult
                    .onSuccess {
                        // goLive is already called inside joinRoom for host.
                        // DB insert is independent of Stream — run it while Stream finishes setup.
                        val discoveryResult = roomDiscoveryRepository.createRoom(
                            id = roomId,
                            title = roomTitle,
                            category = "VOQAL ROOM"
                        )

                        discoveryResult
                            .onSuccess {
                                _state.update { it.copy(isCreatingRoom = false, showRoomTypeSheet = false) }
                                _events.send(RoomEvent.RoomCreated(roomId))
                            }
                            .onFailure { error ->
                                _state.update { it.copy(isCreatingRoom = false) }
                                _events.send(RoomEvent.Error(error.toUiText()))
                            }
                    }
                    .onFailure { error ->
                        _state.update { it.copy(isCreatingRoom = false) }
                        _events.send(RoomEvent.Error(error.toUiText()))
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isCreatingRoom = false) }
                _events.send(RoomEvent.Error(UiText.DynamicString("App Crash: ${e.message ?: "Unknown"}")))
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
