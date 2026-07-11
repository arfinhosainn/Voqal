package app.voqal.com.feature.room

import android.app.Application
import android.content.Context
import app.voqal.com.core.di.VoqalNotificationHandler
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.notifications.NotificationConfig
import io.getstream.video.android.model.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class StreamClientHolder(
    private val context: Context,
    private val apiKey: String
) {
    @Volatile private var client: StreamVideo? = null
    @Volatile var currentUserId: String? = null
        private set
    private val mutex = Mutex()

    fun isConnected(): Boolean = client != null && currentUserId != null

    suspend fun connectUser(
        userId: String,
        name: String,
        imageUrl: String?,
        token: String
    ): StreamVideo = mutex.withLock {
        if (currentUserId != null && currentUserId != userId) {
            disconnect()
        }
        if (currentUserId == userId && client != null) {
            return@withLock client!!
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
        built
    }

    fun currentClient(): StreamVideo? = client

    fun disconnect() {
        StreamVideo.removeClient()
        client = null
        currentUserId = null
    }
}
