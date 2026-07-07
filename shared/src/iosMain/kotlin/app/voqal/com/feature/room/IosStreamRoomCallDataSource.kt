package app.voqal.com.feature.room

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.RoomConnectionState
import app.voqal.com.feature.room.domain.RoomInfo
import app.voqal.com.feature.room.domain.RoomParticipant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StreamRoomCallDataSource(
    private val connectionManager: StreamVideoConnectionManager
) : RoomCallRemoteDataSource {

    private val _connectionState = MutableStateFlow(RoomConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<RoomConnectionState> = _connectionState.asStateFlow()

    private val _roomInfo = MutableStateFlow(RoomInfo(null, null, isBackstage = true))
    override val roomInfo: StateFlow<RoomInfo> = _roomInfo.asStateFlow()

    private val _participants = MutableStateFlow<List<RoomParticipant>>(emptyList())
    override val participants: StateFlow<List<RoomParticipant>> = _participants.asStateFlow()

    private val _activeSpeakerId = MutableStateFlow<String?>(null)
    override val activeSpeakerId: StateFlow<String?> = _activeSpeakerId.asStateFlow()

    private val _isMicrophoneEnabled = MutableStateFlow(false)
    override val isMicrophoneEnabled: StateFlow<Boolean> = _isMicrophoneEnabled.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    override val isHost: StateFlow<Boolean> = _isHost.asStateFlow()

    override suspend fun joinRoom(
        roomId: String,
        asHost: Boolean,
        title: String?,
        description: String?
    ): EmptyResult<RoomCallError> {
        return Result.Success(Unit)
    }

    override suspend fun goLive(): EmptyResult<RoomCallError> {
        return Result.Success(Unit)
    }

    override suspend fun stopLive(): EmptyResult<RoomCallError> {
        return Result.Success(Unit)
    }

    override suspend fun setMicrophoneEnabled(enabled: Boolean) {
        _isMicrophoneEnabled.value = enabled
    }

    override suspend fun raiseHand(): EmptyResult<RoomCallError> {
        return Result.Success(Unit)
    }

    override suspend fun lowerHand(): EmptyResult<RoomCallError> {
        return Result.Success(Unit)
    }

    override suspend fun leaveRoom() {
        _connectionState.value = RoomConnectionState.DISCONNECTED
    }

    override suspend fun endRoom(): EmptyResult<RoomCallError> {
        leaveRoom()
        return Result.Success(Unit)
    }
}
