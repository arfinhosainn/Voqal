package app.voqal.com.feature.chat.presentation

sealed interface ChatAction {
    data class InputChanged(val text: String) : ChatAction
    data object Send : ChatAction
    data object OpenAttachment : ChatAction
    data object OpenEmoji : ChatAction
    data class Retry(val messageId: String) : ChatAction
    data object Dismiss : ChatAction
}
