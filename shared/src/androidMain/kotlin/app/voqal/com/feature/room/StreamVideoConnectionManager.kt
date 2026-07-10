package app.voqal.com.feature.room

import android.content.Context
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.notifications.NotificationConfig
import io.getstream.video.android.model.User
import android.app.Application
import app.voqal.com.core.di.VoqalNotificationHandler

class StreamVideoConnectionManager(
    private val context: Context,
    private val apiKey: String
) {
    @Volatile private var client: StreamVideo? = null
    @Volatile var currentUserId: String? = null
        private set

    fun isConnected(): Boolean = client != null && currentUserId != null

    fun connectUser(
        userId: String,
        name: String,
        imageUrl: String?,
        token: String
    ): Result<StreamVideo, RoomCallError> {
        return try {
            // If already connected with different user, disconnect first
            if (currentUserId != null && currentUserId != userId) {
                disconnect()
            }

            // If already connected with same user, return existing client
            if (currentUserId == userId && client != null) {
                return Result.Success(client!!)
            }

            val user = User(id = userId, name = name, image = imageUrl ?: "")
            val built = StreamVideoBuilder(
                context = context,
                apiKey = apiKey,
                geo = GEO.GlobalEdgeNetwork,
                user = user,
                token = token,
                notificationConfig = NotificationConfig(
                    notificationHandler = VoqalNotificationHandler(context.applicationContext as Application)
                )
            ).build()

            client = built
            currentUserId = userId
            Result.Success(built)
        } catch (@Suppress("UNUSED_PARAMETER") e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    fun currentClient(): StreamVideo? = client

    fun disconnect() {
        StreamVideo.removeClient()
        client = null
        currentUserId = null
    }
}



