package app.voqal.com.feature.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: String,
    @SerialName("room_id")
    val roomId: String,
    @SerialName("user_id")
    val userId: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
    // Joined profile data
    val profiles: ProfileChatDto? = null
)

@Serializable
data class ProfileChatDto(
    val username: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("avatar_path")
    val avatarPath: String? = null
)
