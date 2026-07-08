package app.voqal.com.feature.chat.domain.usecase

import app.voqal.com.feature.chat.domain.model.ChatMessage
import app.voqal.com.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ObserveMessagesUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(roomId: String): Flow<List<ChatMessage>> {
        return repository.observeMessages(roomId)
    }
}
