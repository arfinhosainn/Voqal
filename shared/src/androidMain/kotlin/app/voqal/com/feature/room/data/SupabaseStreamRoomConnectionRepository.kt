package app.voqal.com.feature.room.data

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.StreamClientHolder
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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

private data class StreamUserData(
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val token: String
)

class SupabaseStreamRoomConnectionRepository(
    private val supabaseClient: SupabaseClient,
    private val clientHolder: StreamClientHolder
) : StreamRoomConnectionRepository {

    override val currentUserId: String?
        get() = clientHolder.currentUserId

    override suspend fun ensureUserConnected(): EmptyResult<RoomCallError> {
        return try {
            if (clientHolder.isConnected()) {
                return Result.Success(Unit)
            }

            val currentUser = try {
                supabaseClient.auth.currentUserOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: return Result.Error(RoomCallError.NOT_CONNECTED)

            val userId = currentUser.id

            val userData = fetchStreamUserData(userId)
                ?: return Result.Error(RoomCallError.NOT_CONNECTED)

            clientHolder.connectUser(
                userId = userData.userId,
                name = userData.userName,
                imageUrl = userData.avatarUrl,
                token = userData.token
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    private suspend fun fetchStreamUserData(userId: String): StreamUserData? = coroutineScope {
        val profileDeferred = async { fetchProfile(userId) }
        val tokenDeferred = async { fetchStreamToken(userId) }

        val profile = profileDeferred.await()
            ?: return@coroutineScope null

        val tokenResponse = tokenDeferred.await()
            ?: return@coroutineScope null

        val avatarUrlDeferred = async {
            if (profile.avatarPath != null) {
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
        }

        val userName = profile.username ?: profile.firstName ?: "User"
        val avatarUrl = avatarUrlDeferred.await()

        StreamUserData(
            userId = userId,
            userName = userName,
            avatarUrl = avatarUrl,
            token = tokenResponse.token
        )
    }

    private suspend fun fetchProfile(userId: String): ProfileDto? {
        return try {
            val profiles = supabaseClient.postgrest
                .from("profiles")
                .select(columns = Columns.list("id", "username", "first_name", "last_name", "avatar_path")) {
                    filter { eq("id", userId) }
                }
                .decodeList<ProfileDto>()

            if (profiles.isEmpty()) {
                println("Supabase Error: No profile found for user $userId")
                null
            } else {
                profiles.first()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchStreamToken(userId: String): StreamTokenResponseForConnection? {
        return try {
            val response = supabaseClient.functions.invoke(
                function = "get-stream-token",
                body = StreamTokenRequest(userId = userId)
            )
            if (response.status.value !in 200..299) {
                val errorBody = try { response.bodyAsText() } catch (e: Exception) { "Could not read error body" }
                println("Edge Function Error: ${response.status} - $errorBody")
                null
            } else {
                response.body<StreamTokenResponseForConnection>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
