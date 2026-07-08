package app.voqal.com.feature.chat.domain.usecase

import app.voqal.com.feature.chat.domain.repository.ChatRepository

class LoadMoreMessagesUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(roomId: String) {
        repository.loadMoreMessages(roomId)
    }
}
