package app.voqal.com.feature.room.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

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
                
                // 1. Ensure user is connected to Stream
                val connectionResult = connectionRepository.ensureUserConnected()
                
                if (connectionResult is app.voqal.com.core.domain.Result.Failure) {
                    _state.update { it.copy(isCreatingRoom = false) }
                    _events.send(RoomEvent.Error(connectionResult.error.toUiText()))
                    return@launch
                }

                // 2. Create the room in Stream
                val roomId = "room_${Random.nextInt(100000, 999999)}"
                val result = roomCallDataSource.joinRoom(
                    roomId = roomId,
                    asHost = true,
                    title = _state.value.selectedRoomType.title
                )

                result
                    .onSuccess {
                        // 3. Register room in Discovery (Supabase)
                        roomDiscoveryRepository.createRoom(
                            id = roomId,
                            title = _state.value.selectedRoomType.title,
                            category = "VOQAL ROOM"
                        )

                        _state.update { it.copy(isCreatingRoom = false, showRoomTypeSheet = false) }
                        _events.send(RoomEvent.RoomCreated(roomId))
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
