package app.voqal.com.feature.chat.presentation

sealed interface ChatEffect {
    data object HideKeyboard : ChatEffect
    data class ShowError(val message: String) : ChatEffect
    data object ScrollToBottom : ChatEffect
}
