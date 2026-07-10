package app.voqal.com.feature.chat.domain.usecase

import app.voqal.com.core.domain.Result
import app.voqal.com.feature.chat.domain.model.ChatError
import app.voqal.com.feature.chat.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(roomId: String, userId: String, text: String): Result<Unit, ChatError> {
        val trimmed = text.trim()
        
        if (trimmed.isEmpty()) {
            return Result.Success(Unit)
        }
        
        if (trimmed.length > 500) {
            return Result.Error(ChatError.MESSAGE_TOO_LONG)
        }
        
        return repository.sendMessage(roomId, userId, trimmed)
    }
}
