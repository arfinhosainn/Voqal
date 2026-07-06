package app.voqal.com.feature.room.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: String,
    val title: String,
    val category: String,
    @SerialName("listener_count")
    val listenerCount: Int = 0,
    @SerialName("comment_count")
    val commentCount: Int = 0,
    @SerialName("participant_preview")
    val participantPreview: List<ParticipantPreviewDto> = emptyList(),
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("last_activity_at")
    val lastActivityAt: String? = null
)
