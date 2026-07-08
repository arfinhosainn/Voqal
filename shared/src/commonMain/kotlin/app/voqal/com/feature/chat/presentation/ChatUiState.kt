package app.voqal.com.feature.chat.presentation

import app.voqal.com.feature.chat.presentation.model.ChatMessageUi

data class ChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val hasReachedEnd: Boolean = false,
    val error: String? = null
)
