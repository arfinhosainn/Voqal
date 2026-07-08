package app.voqal.com.feature.chat.data.mapper

import app.voqal.com.feature.chat.data.dto.ChatMessageDto
import app.voqal.com.feature.chat.domain.model.ChatContent
import app.voqal.com.feature.chat.domain.model.ChatMessage
import app.voqal.com.feature.chat.domain.model.MessageStatus
import kotlinx.datetime.Instant

fun ChatMessageDto.toDomain(currentUserId: String?): ChatMessage {
    return ChatMessage(
        id = id,
        senderId = userId,
        senderName = profiles?.username ?: profiles?.firstName ?: "User",
        senderAvatar = profiles?.avatarPath?.let { path ->
             "https://bykulndzmnkfkgypgaae.supabase.co/storage/v1/object/public/avatars/$path"
        },
        content = ChatContent.Text(content),
        timestamp = Instant.parse(createdAt),
        status = MessageStatus.Sent,
        isMine = userId == currentUserId
    )
}
