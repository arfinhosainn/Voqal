package app.voqal.com.feature.room



import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomCallRemoteDataSource
import app.voqal.com.feature.room.domain.RoomConnectionState
import app.voqal.com.feature.room.domain.RoomInfo
import app.voqal.com.feature.room.domain.RoomParticipant
import io.getstream.android.video.generated.models.MemberRequest
import io.getstream.video.android.core.Call
import app.voqal.com.core.domain.Result
import io.getstream.video.android.core.CreateCallOptions
import io.getstream.video.android.core.ParticipantState
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.model.Reaction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StreamRoomCallDataSource(
    private val connectionManager: StreamVideoConnectionManager
) : RoomCallRemoteDataSource {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var call: Call? = null
    private var observerJob: Job? = null

    private val _connectionState = MutableStateFlow(RoomConnectionState.DISCONNECTED)
    override val connectionState = _connectionState.asStateFlow()

    private val _roomInfo = MutableStateFlow(RoomInfo(null, null, isBackstage = true))
    override val roomInfo = _roomInfo.asStateFlow()

    private val _participants = MutableStateFlow<List<RoomParticipant>>(emptyList())
    override val participants = _participants.asStateFlow()

    private val _activeSpeakerId = MutableStateFlow<String?>(null)
    override val activeSpeakerId = _activeSpeakerId.asStateFlow()

    private val _isMicrophoneEnabled = MutableStateFlow(false)
    override val isMicrophoneEnabled = _isMicrophoneEnabled.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    override val isHost = _isHost.asStateFlow()

    override suspend fun joinRoom(
        roomId: String,
        asHost: Boolean,
        title: String?,
        description: String?
    ): EmptyResult<RoomCallError> {
        val client = connectionManager.currentClient()
            ?: return Result.Error(RoomCallError.NOT_CONNECTED)
        val userId = connectionManager.currentUserId
            ?: return Result.Error(RoomCallError.NOT_CONNECTED)
        if (call != null) return Result.Error(RoomCallError.ALREADY_IN_ROOM)

        val newCall = client.call("audio_room", roomId)
        call = newCall

        return try {
            val joinResult = if (asHost) {
                newCall.join(
                    create = true,
                    createOptions = CreateCallOptions(
                        members = listOf(
                            MemberRequest(
                                userId = userId,
                                role = "host",
                                custom = emptyMap()
                            )
                        ),
                        custom = buildMap {
                            title?.let { put("title", it) }
                            description?.let { put("description", it) }
                        }
                    )
                )
            } else {
                // For participants, we also use create = true to ensure they are added
                // as members if the room type requires it, but with a restricted role.
                newCall.join(
                    create = true,
                    createOptions = CreateCallOptions(
                        members = listOf(
                            MemberRequest(
                                userId = userId,
                                role = "user",
                                custom = emptyMap()
                            )
                        )
                    )
                )
            }

            var failed = false
            joinResult.onError { failed = true }

            if (failed) {
                call = null
                Result.Error(RoomCallError.JOIN_FAILED)
            } else {
                observe(newCall)
                if (asHost) {
                    try {
                        newCall.goLive()
                    } catch (e: Exception) {
                        e.printStackTrace() // Log but don't fail join if goLive fails
                    }
                }
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            call = null
            Result.Error(RoomCallError.JOIN_FAILED)
        }
    }

    override suspend fun goLive(): EmptyResult<RoomCallError> {
        val active = call ?: return Result.Error(RoomCallError.NOT_CONNECTED)
        return try {
            active.goLive(); Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun stopLive(): EmptyResult<RoomCallError> {
        val active = call ?: return Result.Error(RoomCallError.NOT_CONNECTED)
        return try {
            active.stopLive(); Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun setMicrophoneEnabled(enabled: Boolean) {
        call?.microphone?.setEnabled(enabled)
    }

    override suspend fun raiseHand(): EmptyResult<RoomCallError> {
        val active = call ?: return Result.Error(RoomCallError.NOT_CONNECTED)
        return try {
            // We can add a custom attribute or just send the reaction.
            // If we want to ensure a 'new' reaction is seen, we can send it again.
            active.sendReaction("raised-hand")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun lowerHand(): EmptyResult<RoomCallError> {
        if (call == null) return Result.Error(RoomCallError.NOT_CONNECTED)
        return try {
            // For now, we'll just return success as a placeholder if no direct API is found.
            // In a real scenario, this might involve deleting a reaction or updating a custom state.
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun leaveRoom() {
        observerJob?.cancel()
        observerJob = null
        call?.leave()
        call = null
        _connectionState.value = RoomConnectionState.DISCONNECTED
        _participants.value = emptyList()
        _activeSpeakerId.value = null
        _roomInfo.value = RoomInfo(null, null, isBackstage = true)
        _isMicrophoneEnabled.value = false
        _isHost.value = false
    }

    override suspend fun endRoom(): EmptyResult<RoomCallError> {
        val active = call ?: return Result.Error(RoomCallError.NOT_CONNECTED)
        return try {
            active.end()
            leaveRoom()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    fun close() {
        observerJob?.cancel()
        scope.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observe(call: Call) {
        observerJob?.cancel()
        observerJob = scope.launch {
            launch {
                call.state.connection.collect { _connectionState.value = it.toDomain() }
            }
            launch {
                combine(call.state.custom, call.state.backstage) { custom, backstage ->
                    RoomInfo(
                        title = custom["title"] as? String,
                        description = custom["description"] as? String,
                        isBackstage = backstage
                    )
                }.collect { _roomInfo.value = it }
            }
            launch {
                call.state.participants
                    .flatMapLatest { it.toRoomParticipantsFlow() }
                    .collect { _participants.value = it }
            }
            launch {
                call.state.activeSpeakers.collect {
                    _activeSpeakerId.value = it.firstOrNull()?.sessionId
                }
            }
            launch {
                call.microphone.isEnabled.collect { _isMicrophoneEnabled.value = it }
            }
            launch {
                call.state.me
                    .flatMapLatest { me ->
                        me?.roles ?: flowOf(emptyList())
                    }
                    .collect { roles ->
                        _isHost.value = roles.contains("host")
                    }
            }
        }
    }
}

private fun RealtimeConnection.toDomain(): RoomConnectionState = when (this) {
    RealtimeConnection.Connected -> RoomConnectionState.CONNECTED
    is RealtimeConnection.Reconnecting -> RoomConnectionState.RECONNECTING
    is RealtimeConnection.Failed -> RoomConnectionState.FAILED
    RealtimeConnection.Disconnected -> RoomConnectionState.DISCONNECTED
    else -> RoomConnectionState.CONNECTING // PreJoin and any other pre-connect states
}

private fun List<ParticipantState>.toRoomParticipantsFlow(): Flow<List<RoomParticipant>> {
    if (isEmpty()) return flowOf(emptyList())
    val perParticipant = map { p ->
        combine(
            p.image,
            p.userNameOrId,
            p.speaking,
            p.audioEnabled,
            p.roles,
            p.reactions
        ) { args: Array<Any?> ->
            val image = args[0] as? String
            val name = args[1] as String
            val speaking = args[2] as Boolean
            val audio = args[3] as Boolean
            val roles = args[4] as List<String>
            val reactions = args[5] as List<*>
            // Filter reactions of type "raised-hand"
            val handRaisedReactions = reactions.mapNotNull { it as? Reaction }
                .filter { it.response.type == "raised-hand" }
            
            val isHandRaised = handRaisedReactions.isNotEmpty()
            
            // If multiple reactions exist, take the newest one's timestamp
            // or just use the current time if the reaction is very recent
            val handRaisedTimestamp = handRaisedReactions
                .maxByOrNull { it.createdAt }?.createdAt ?: 0L

            RoomParticipant(
                sessionId = p.sessionId,
                userId = p.userId.value,
                name = name,
                imageUrl = image,
                role = roles.firstOrNull() ?: "listener",
                isSpeaking = speaking,
                isAudioEnabled = audio,
                isHandRaised = isHandRaised,
                handRaisedTimestamp = handRaisedTimestamp
            )
        }
    }
    return combine(perParticipant) { it.toList() }
}
