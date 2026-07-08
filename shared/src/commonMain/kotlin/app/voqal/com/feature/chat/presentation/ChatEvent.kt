package app.voqal.com.feature.chat.presentation

sealed interface ChatEvent {
    data class InputChanged(val text: String) : ChatEvent
    data object Send : ChatEvent
    data object OpenAttachment : ChatEvent
    data object OpenEmoji : ChatEvent
    data class Retry(val messageId: String) : ChatEvent
    data object Dismiss : ChatEvent
}
