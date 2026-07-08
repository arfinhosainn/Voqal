package app.voqal.com.feature.chat.presentation.model

import app.voqal.com.feature.chat.domain.model.ChatContent
import app.voqal.com.feature.chat.domain.model.MessageStatus
import kotlinx.datetime.Instant

data class ChatMessageUi(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String?,
    val content: ChatContent,
    val timestamp: Instant,
    val status: MessageStatus,
    val isMine: Boolean
)
