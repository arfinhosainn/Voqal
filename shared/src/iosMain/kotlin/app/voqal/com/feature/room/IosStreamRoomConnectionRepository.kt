package app.voqal.com.feature.room

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository

class IosStreamRoomConnectionRepository(
    private val connectionManager: StreamVideoConnectionManager
) : StreamRoomConnectionRepository {

    override val currentUserId: String?
        get() = connectionManager.currentUserId

    override suspend fun ensureUserConnected(): EmptyResult<RoomCallError> {
        return Result.Success(Unit)
    }
}
