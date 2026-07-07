package app.voqal.com.feature.room

import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError

/**
 * Placeholder for iOS Stream Video connection management.
 */
class StreamVideoConnectionManager(
    private val apiKey: String
) {
    var currentUserId: String? = null
        private set

    fun isConnected(): Boolean = false

    fun connectUser(
        userId: String,
        name: String,
        imageUrl: String?,
        token: String
    ): Result<Unit, RoomCallError> {
        currentUserId = userId
        return Result.Success(Unit)
    }

    fun disconnect() {
        currentUserId = null
    }
}
