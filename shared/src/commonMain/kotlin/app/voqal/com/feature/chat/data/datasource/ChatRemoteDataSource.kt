package app.voqal.com.feature.chat.data.datasource

import app.voqal.com.feature.chat.data.dto.ChatMessageDto
import kotlinx.coroutines.flow.Flow

interface ChatRemoteDataSource {
    fun observeMessages(roomId: String): Flow<List<ChatMessageDto>>
    suspend fun sendMessage(roomId: String, userId: String, text: String)
}
