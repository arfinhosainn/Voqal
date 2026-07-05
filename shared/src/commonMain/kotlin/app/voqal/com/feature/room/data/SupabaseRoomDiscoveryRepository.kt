package app.voqal.com.feature.room.data

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        println("DEBUG: ROOM FLOW STARTING")
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
        val channel = supabaseClient.channel("rooms_discovery")
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "rooms"
        }

        try {
            channel.subscribe()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val job = launch {
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

        try {
            awaitClose {
                println("DEBUG: ROOM FLOW CLOSING")
                job.cancel()
            }
        } finally {
            withContext(NonCancellable) {
                try {
                    supabaseClient.realtime.removeChannel(channel)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
