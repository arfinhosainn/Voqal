package app.voqal.com.feature.room

import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError
import io.getstream.video.android.core.StreamVideo

class StreamVideoConnectionManager(
    private val clientHolder: StreamClientHolder
) {
    val currentUserId: String?
        get() = clientHolder.currentUserId

    fun isConnected(): Boolean = clientHolder.isConnected()

    suspend fun connectUser(
        userId: String,
        name: String,
        imageUrl: String?,
        token: String
    ): Result<StreamVideo, RoomCallError> {
        return try {
            val client = clientHolder.connectUser(userId, name, imageUrl, token)
            Result.Success(client)
        } catch (@Suppress("UNUSED_PARAMETER") e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    fun currentClient(): StreamVideo? = clientHolder.currentClient()

    fun disconnect() = clientHolder.disconnect()
}
