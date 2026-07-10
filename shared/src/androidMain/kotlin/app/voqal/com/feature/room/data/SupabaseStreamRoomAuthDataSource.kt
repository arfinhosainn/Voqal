package app.voqal.com.feature.room.data

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.StreamRoomAuthDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class StreamTokenRequestAuth(
    @SerialName("user_id")
    val userId: String
)

@Serializable
private data class StreamTokenResponseAuth(
    val token: String
)

class SupabaseStreamRoomAuthDataSource(
    private val supabaseClient: SupabaseClient
) : StreamRoomAuthDataSource {

    override suspend fun getStreamToken(): EmptyResult<RoomCallError> {
        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.Error(RoomCallError.NOT_CONNECTED)

            // Call Supabase Edge Function to generate Stream Video token
            val response = supabaseClient.functions.invoke(
                function = "get-stream-token",
                body = StreamTokenRequestAuth(userId = userId)
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(RoomCallError.UNKNOWN)
        }
    }
}


