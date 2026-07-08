package app.voqal.com.feature.chat.domain.model

import kotlinx.datetime.Instant

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String?,
    val content: ChatContent,
    val timestamp: Instant,
    val status: MessageStatus,
    val isMine: Boolean
)
