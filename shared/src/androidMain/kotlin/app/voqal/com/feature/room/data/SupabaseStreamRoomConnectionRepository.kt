package app.voqal.com.feature.room.data

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.StreamVideoConnectionManager
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlin.time.Duration.Companion.hours
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class ProfileDto(
    val id: String,
    val username: String?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("avatar_path")
    val avatarPath: String?
)

@Serializable
internal data class StreamTokenRequest(
    @SerialName("user_id")
    val userId: String
)

@Serializable
internal data class StreamTokenResponseForConnection(
    val token: String
)

class SupabaseStreamRoomConnectionRepository(
    private val supabaseClient: SupabaseClient,
    private val connectionManager: StreamVideoConnectionManager
) : StreamRoomConnectionRepository {

    override suspend fun ensureUserConnected(): EmptyResult<RoomCallError> {
        return try {
            // If already connected, return success
            if (connectionManager.isConnected()) {
                return Result.Success(Unit)
            }

            // Get current user - Wrap in try-catch to handle the session load crash
            val currentUser = try {
                supabaseClient.auth.currentUserOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: return Result.Failure(RoomCallError.NOT_CONNECTED)

            val userId = currentUser.id

            // Get user profile data
            val profiles = supabaseClient.postgrest
                .from("profiles")
                .select(columns = Columns.list("id", "username", "first_name", "last_name", "avatar_path")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeList<ProfileDto>()

            if (profiles.isEmpty()) {
                println("Supabase Error: No profile found for user $userId")
                return Result.Failure(RoomCallError.NOT_CONNECTED)
            }

            val profile = profiles.first()
            val userName = profile.username ?: profile.firstName ?: "User"

            // Get Stream Video token from Edge Function
            val response = supabaseClient.functions.invoke(
                function = "get-stream-token",
                body = StreamTokenRequest(userId = userId)
            )
            
            if (response.status.value !in 200..299) {
                val errorBody = try { response.bodyAsText() } catch (e: Exception) { "Could not read error body" }
                println("Edge Function Error: ${response.status} - $errorBody")
                return Result.Failure(RoomCallError.UNKNOWN)
            }

            val tokenResponse = response.body<StreamTokenResponseForConnection>()

            // Get avatar URL if exists
            val avatarUrl = if (profile.avatarPath != null) {
                try {
                    supabaseClient.storage
                        .from("avatars")
                        .createSignedUrl(profile.avatarPath, expiresIn = 24.hours)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            // Connect user to Stream Video
            val connectResult = connectionManager.connectUser(
                userId = userId,
                name = userName,
                imageUrl = avatarUrl,
                token = tokenResponse.token
            )

            when (connectResult) {
                is Result.Success -> Result.Success(Unit)
                is Result.Failure -> Result.Failure(connectResult.error)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(RoomCallError.UNKNOWN)
        }
    }
}
