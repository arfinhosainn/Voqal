package app.voqal.com.feature.chat.data

import app.voqal.com.core.domain.Result
import app.voqal.com.feature.chat.data.datasource.ChatRemoteDataSource
import app.voqal.com.feature.chat.data.mapper.toDomain
import app.voqal.com.feature.chat.domain.model.ChatError
import app.voqal.com.feature.chat.domain.model.ChatMessage
import app.voqal.com.feature.chat.domain.repository.ChatRepository
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val connectionRepository: StreamRoomConnectionRepository
) : ChatRepository {

    override fun observeMessages(roomId: String): Flow<List<ChatMessage>> {
        return remoteDataSource.observeMessages(roomId).map { dtos ->
            val currentUserId = connectionRepository.currentUserId
            dtos.map { it.toDomain(currentUserId) }
        }
    }

    override suspend fun sendMessage(roomId: String, text: String): Result<Unit, ChatError> {
        return try {
            remoteDataSource.sendMessage(roomId, text)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(ChatError.NETWORK)
        }
    }

    override suspend fun loadMoreMessages(roomId: String) {
        // Future implementation
    }
}
