package app.voqal.com.feature.room


import android.content.Context
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class StreamVideoSessionManager(
    private val context: Context,
    private val apiKey: String
) {
    @Volatile private var client: StreamVideo? = null

    @Volatile var currentUserId: String? = null
        private set

    fun connectUser(
        userId: String,
        name: String,
        imageUrl: String?,
        token: String
    ): Result<StreamVideo, RoomCallError> = try {
        val user = User(id = userId, name = name, image = imageUrl ?: "")
        val built = StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = token,
        ).build()
        client = built
        currentUserId = userId
        Result.Success(built)
    } catch (e: Exception) {
        Result.Failure(RoomCallError.UNKNOWN)
    }

    fun currentClient(): StreamVideo? = client

    fun disconnect() {
        StreamVideo.removeClient()
        client = null
        currentUserId = null
    }
}