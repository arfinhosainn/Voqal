package app.voqal.com.feature.chat.domain.repository

import app.voqal.com.core.domain.Result
import app.voqal.com.feature.chat.domain.model.ChatError
import app.voqal.com.feature.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(roomId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(roomId: String, userId: String, text: String): Result<Unit, ChatError>
    suspend fun loadMoreMessages(roomId: String)
}
