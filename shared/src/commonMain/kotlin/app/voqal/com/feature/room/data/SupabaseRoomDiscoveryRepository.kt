package app.voqal.com.feature.room.data

import app.voqal.com.core.domain.EmptyResult
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.room.data.dto.RoomDto
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.ParticipantUi
import app.voqal.com.feature.room.domain.RoomCallError
import app.voqal.com.feature.room.domain.RoomDiscoveryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseRoomDiscoveryRepository(
    private val supabaseClient: SupabaseClient
) : RoomDiscoveryRepository {

    override fun getRoomsFlow(): Flow<List<NewsRoomUi>> = channelFlow {
        println("DEBUG: ROOM FLOW STARTING")
        // 1. Initial fetch from the view
        suspend fun fetchRooms() {
            try {
                val response = supabaseClient.postgrest.from("rooms")
                    .select {
                        filter {
                            eq("status", "live")
                        }
                        order("listener_count", Order.DESCENDING)
                        order("last_activity_at", Order.DESCENDING)
                    }
                
                val rooms = response.decodeList<RoomDto>()
                
                send(rooms.map { dto: RoomDto ->
                    NewsRoomUi(
                        id = dto.id,
                        category = dto.category,
                        title = dto.title,
                        participants = dto.participantPreview.map { p ->
                            ParticipantUi(
                                id = p.id,
                                name = p.name,
                                avatarUrl = p.avatarPath?.let { path ->
                                    "https://bykulndzmnkfkgypgaae.supabase.co/storage/v1/object/public/avatars/$path"
                                },
                                countryCode = p.countryCode
                            )
                        },
                        listenerCount = dto.listenerCount,
                        commentCount = dto.commentCount
                    )
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fetchRooms()

        // 2. Real-time updates
        val channel = supabaseClient.channel("rooms_discovery")
        val roomChanges = channel.postgresChangeFlow<PostgresAction>(schema = "public") { table = "rooms" }
        val sessionChanges = channel.postgresChangeFlow<PostgresAction>(schema = "public") { table = "room_sessions" }

        try {
            channel.subscribe()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val job = launch { roomChanges.collect { fetchRooms() } }
        val sessionJob = launch { sessionChanges.collect { fetchRooms() } }

        try {
            awaitClose {
                println("DEBUG: ROOM FLOW CLOSING")
                job.cancel()
                sessionJob.cancel()
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
                mapOf(
                    "id" to id,
                    "title" to title,
                    "category" to category
                    // host_id is handled by SQL default auth.uid()
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun deleteRoom(id: String): EmptyResult<RoomCallError> {
        return try {
            supabaseClient.postgrest.from("rooms").delete {
                filter { eq("id", id) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun joinRoom(roomId: String): EmptyResult<RoomCallError> {
        return try {
            supabaseClient.postgrest.rpc("join_room", buildJsonObject {
                put("p_room_id", roomId)
            })
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(RoomCallError.UNKNOWN)
        }
    }

    override suspend fun leaveRoom(roomId: String): EmptyResult<RoomCallError> {
        return try {
            supabaseClient.postgrest.rpc("leave_room", buildJsonObject {
                put("p_room_id", roomId)
            })
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(RoomCallError.UNKNOWN)
        }
    }
}
