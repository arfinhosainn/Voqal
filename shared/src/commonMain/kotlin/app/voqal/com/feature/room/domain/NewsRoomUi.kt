package app.voqal.com.feature.room.domain

import androidx.compose.runtime.Immutable

@Immutable
data class NewsRoomUi(
    val id: String,
    val category: String,
    val title: String,
    val participants: List<ParticipantUi>,
    val listenerCount: Int,
    val commentCount: Int,
)