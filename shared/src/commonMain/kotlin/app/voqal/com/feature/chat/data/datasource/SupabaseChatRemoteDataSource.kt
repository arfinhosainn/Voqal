package app.voqal.com.feature.chat.data.datasource

import app.voqal.com.feature.chat.data.dto.ChatMessageDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupabaseChatRemoteDataSource(
    private val supabaseClient: SupabaseClient
) : ChatRemoteDataSource {

    override fun observeMessages(roomId: String): Flow<List<ChatMessageDto>> = channelFlow {
        val currentMessages = mutableListOf<ChatMessageDto>()

        // 1. Initial Fetch (Latest 50)
        try {
            val response = supabaseClient.postgrest.from("room_messages")
                .select(columns = Columns.raw("*, profiles(username, first_name, avatar_path)")) {
                    filter {
                        eq("room_id", roomId)
                    }
                    order("created_at", Order.DESCENDING)
                    limit(50)
                }
            val initialMessages = response.decodeList<ChatMessageDto>()
            currentMessages.addAll(initialMessages)
            send(currentMessages.toList())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Real-time Subscription (Incremental Append)
        val channel = supabaseClient.channel("chat_$roomId")
        val messageChanges = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "room_messages"
            // Note: Room filtering is applied here via string property in some versions
            // or we handle it manually in the collection block for maximum safety
        }

        val job = launch {
            messageChanges.collect { action ->
                val recordRoomId = action.record["room_id"]?.toString()
                if (recordRoomId != roomId) return@collect

                val messageId = action.record["id"]?.toString() ?: return@collect
                
                // Fetch the full record (with profile) for the new arrival
                try {
                    val profileResponse = supabaseClient.postgrest.from("room_messages")
                        .select(columns = Columns.raw("*, profiles(username, first_name, avatar_path)")) {
                            filter {
                                eq("id", messageId)
                            }
                        }.decodeSingle<ChatMessageDto>()
                    
                    currentMessages.add(0, profileResponse)
                    send(currentMessages.toList())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        channel.subscribe()

        awaitClose {
            job.cancel()
            launch {
                withContext(NonCancellable) {
                    supabaseClient.realtime.removeChannel(channel)
                }
            }
        }
    }

    override suspend fun sendMessage(roomId: String, text: String) {
        supabaseClient.postgrest.from("room_messages").insert(
            mapOf(
                "room_id" to roomId,
                "content" to text
            )
        )
    }
}
