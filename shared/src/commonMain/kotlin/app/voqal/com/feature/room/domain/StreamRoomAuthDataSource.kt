package app.voqal.com.feature.room.domain

import app.voqal.com.core.domain.EmptyResult

interface StreamRoomAuthDataSource {
    /**
     * Gets a Stream Video token for the current user.
     * This token is required to connect to the Stream Video service.
     */
    suspend fun getStreamToken(): EmptyResult<RoomCallError>
}

