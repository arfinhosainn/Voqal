package app.voqal.com.feature.chat.data.datasource

import app.voqal.com.feature.chat.data.dto.ChatMessageDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
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
            val initialMessages = supabaseClient.postgrest.from("room_messages")
                .select {
                    filter { eq("room_id", roomId) }
                    order("created_at", Order.DESCENDING)
                    limit(50)
                }
                .decodeList<ChatMessageDto>()
            currentMessages.addAll(initialMessages)
            send(currentMessages.toList())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Real-time Subscription (Incremental Append)
        // Use unique suffix to avoid collision with stale channels from previous subscriptions
        val channelName = "chat_${roomId}_${kotlin.random.Random.nextLong()}"
        val channel = supabaseClient.channel(channelName)
        val messageChanges = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "room_messages"
        }

        val job = launch {
            messageChanges.collect { action ->
                val recordRoomId = action.record["room_id"]?.toString()
                if (recordRoomId != roomId) return@collect

                val newMessage = action.decodeRecord<ChatMessageDto>()
                currentMessages.add(0, newMessage)
                send(currentMessages.toList())
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

    override suspend fun sendMessage(roomId: String, userId: String, text: String) {
        supabaseClient.postgrest.from("room_messages").insert(
            mapOf(
                "room_id" to roomId,
                "user_id" to userId,
                "content" to text
            )
        )
    }
}
