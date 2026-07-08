package app.voqal.com.feature.chat.domain.model

sealed interface MessageStatus {
    data object Sending : MessageStatus
    data object Sent : MessageStatus
    data object Delivered : MessageStatus
    data object Read : MessageStatus
    data class Failed(val error: String) : MessageStatus
}
