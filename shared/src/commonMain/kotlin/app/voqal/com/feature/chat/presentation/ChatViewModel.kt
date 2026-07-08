package app.voqal.com.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.onFailure
import app.voqal.com.core.domain.onSuccess
import app.voqal.com.feature.chat.domain.model.ChatContent
import app.voqal.com.feature.chat.domain.model.ChatMessage
import app.voqal.com.feature.chat.domain.model.MessageStatus
import app.voqal.com.feature.chat.domain.usecase.ObserveMessagesUseCase
import app.voqal.com.feature.chat.domain.usecase.SendMessageUseCase
import app.voqal.com.feature.chat.presentation.model.ChatMessageUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ChatViewModel(
    private val roomId: String,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    private val _effects = Channel<ChatEffect>()
    val effects = _effects.receiveAsFlow()

    init {
        observeMessages()
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.InputChanged -> {
                _state.update { it.copy(input = event.text) }
            }
            ChatEvent.Send -> sendMessage()
            ChatEvent.OpenAttachment -> { /* Future */ }
            ChatEvent.OpenEmoji -> { /* Future */ }
            is ChatEvent.Retry -> { /* Future */ }
            ChatEvent.Dismiss -> { /* Handled by UI */ }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeMessagesUseCase(roomId).collectLatest { domainMessages ->
                _state.update {
                    it.copy(
                        messages = domainMessages.map { m -> m.toUi() },
                        isLoading = false
                    )
                }
                _effects.send(ChatEffect.ScrollToBottom)
            }
        }
    }

    private fun sendMessage() {
        val content = _state.value.input
        if (content.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(input = "") }
            
            // Note: In a full local echo implementation, we would add the message 
            // to the local list with 'Sending' status before calling the use case.
            // For now, we rely on Supabase Realtime to push it back.
            
            sendMessageUseCase(roomId, content)
                .onFailure { error ->
                    _effects.send(ChatEffect.ShowError("Failed to send: ${error.name}"))
                }
        }
    }

    private fun ChatMessage.toUi(): ChatMessageUi {
        return ChatMessageUi(
            id = id,
            senderId = senderId,
            senderName = senderName,
            senderAvatar = senderAvatar,
            content = content,
            timestamp = timestamp,
            status = status,
            isMine = isMine
        )
    }
}
