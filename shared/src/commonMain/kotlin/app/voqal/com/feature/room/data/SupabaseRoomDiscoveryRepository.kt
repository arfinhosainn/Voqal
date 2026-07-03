package app.voqal.com.feature.room.data

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable

@Serializable
private data class RoomDto(
    val id: String,
    val title: String,
    val category: String,
    val listener_count: Int = 0,
    val comment_count: Int = 0
) {
    fun toNewsRoomUi(): NewsRoomUi = NewsRoomUi(
        id = id,
        title = title,
        category = category,
        participants = emptyList(), 
        listenerCount = listener_count,
        commentCount = comment_count
    )
}

class SupabaseRoomDiscoveryRepository(
    private val supabaseClient: SupabaseClient
) : RoomDiscoveryRepository {

    override fun getRoomsFlow(): Flow<List<NewsRoomUi>> = channelFlow {
        // 1. Initial fetch
        try {
            val initialRooms = supabaseClient.postgrest.from("rooms")
                .select()
                .decodeList<RoomDto>()
            send(initialRooms.map { it.toNewsRoomUi() })
        } catch (e: Exception) {
            e.printStackTrace()
            send(emptyList())
        }

        // 2. Real-time updates
        val channel = supabaseClient.channel("rooms_channel")
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "rooms"
        }

        channel.subscribe()

        changes.collect { 
            try {
                val updatedRooms = supabaseClient.postgrest.from("rooms")
                    .select()
                    .decodeList<RoomDto>()
                send(updatedRooms.map { it.toNewsRoomUi() })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun createRoom(
        id: String,
        title: String,
        category: String
    ): EmptyResult<RoomCallError> {
        return try {
            supabaseClient.postgrest.from("rooms").insert(
                RoomDto(id = id, title = title, category = category)
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun deleteRoom(id: String): EmptyResult<RoomCallError> {
        return try {
            supabaseClient.postgrest.from("rooms").delete {
                filter {
                    eq("id", id)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(RoomCallError.UNKNOWN)
        }
    }
}
