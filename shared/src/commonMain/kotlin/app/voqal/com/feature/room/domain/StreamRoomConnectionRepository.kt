package app.voqal.com.feature.room.domain

import app.voqal.com.core.domain.EmptyResult

interface StreamRoomConnectionRepository {
    val currentUserId: String?

    /**
     * Connects the current user to the Stream Video service.
     * This must be called before attempting to join a room.
     */
    suspend fun ensureUserConnected(): EmptyResult<RoomCallError>
}

