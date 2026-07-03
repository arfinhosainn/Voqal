package app.voqal.com.feature.room.domain

import app.voqal.com.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RoomDiscoveryRepository {
    /**
     * Returns a real-time flow of all active rooms.
     */
    fun getRoomsFlow(): Flow<List<NewsRoomUi>>

    /**
     * Registers a new room in the discovery service.
     */
    suspend fun createRoom(
        id: String,
        title: String,
        category: String
    ): EmptyResult<RoomCallError>

    /**
     * Removes a room from discovery when it ends.
     */
    suspend fun deleteRoom(id: String): EmptyResult<RoomCallError>
}
