package app.voqal.com.feature.room.domain

import app.voqal.com.core.domain.EmptyResult


import kotlinx.coroutines.flow.StateFlow

interface RoomCallRemoteDataSource {
    val connectionState: StateFlow<RoomConnectionState>
    val roomInfo: StateFlow<RoomInfo>
    val participants: StateFlow<List<RoomParticipant>>
    val activeSpeakerId: StateFlow<String?>
    val isMicrophoneEnabled: StateFlow<Boolean>
    val isHost: StateFlow<Boolean>

    suspend fun joinRoom(
        roomId: String,
        asHost: Boolean,
        title: String? = null,
        description: String? = null
    ): EmptyResult<RoomCallError>

    suspend fun goLive(): EmptyResult<RoomCallError>
    suspend fun stopLive(): EmptyResult<RoomCallError>
    suspend fun setMicrophoneEnabled(enabled: Boolean)
    suspend fun leaveRoom()
    suspend fun endRoom(): EmptyResult<RoomCallError>
}