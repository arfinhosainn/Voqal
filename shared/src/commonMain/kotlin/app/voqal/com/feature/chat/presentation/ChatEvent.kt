package app.voqal.com.feature.chat.presentation

sealed interface ChatEvent {
    data object HideKeyboard : ChatEvent
    data class ShowError(val message: String) : ChatEvent
    data object ScrollToBottom : ChatEvent
}
