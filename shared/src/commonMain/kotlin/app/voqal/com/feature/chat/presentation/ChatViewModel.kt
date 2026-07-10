package app.voqal.com.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.onFailure
import app.voqal.com.core.domain.onSuccess
import app.voqal.com.core.utils.UuidUtils
import app.voqal.com.feature.chat.domain.model.ChatContent
import app.voqal.com.feature.chat.domain.model.ChatMessage
import app.voqal.com.feature.chat.domain.model.MessageStatus
import app.voqal.com.feature.chat.domain.usecase.ObserveMessagesUseCase
import app.voqal.com.feature.chat.domain.usecase.SendMessageUseCase
import app.voqal.com.feature.chat.presentation.ChatAction
import app.voqal.com.feature.chat.presentation.ChatEvent
import app.voqal.com.feature.chat.presentation.model.ChatMessageUi
import app.voqal.com.feature.room.domain.StreamRoomConnectionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.time.Clock

class ChatViewModel(
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val connectionRepository: StreamRoomConnectionRepository,
    private val roomId: String  // Single roomId for ViewModel lifetime
) : ViewModel() {

    // roomId is injected by Koin factory at construction time
    // using ChatModule.kt: factory { (roomId: String) -> ChatViewModel(...) }
    // This ensures the SAME ViewModel instance for the same roomId

    private val _observedMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _localMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _input = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)

    private val _events = Channel<ChatEvent>()
    val events = _events.receiveAsFlow()

    val state = combine(
        _observedMessages,
        _localMessages,
        _input,
        _isLoading
    ) { observed, local, input, isLoading ->
        // Combine local and observed messages, use local id as source of truth for order.
        // Prefer local messages in the UI to ensure the optimistic bubble appears immediately.
        // If the server ever sends a matching message, it will have a different id, so no conflict.
        val all = (local + observed)
            .distinctBy { it.id }
            .sortedByDescending { it.timestamp }
        ChatUiState(
            messages = all.map { it.toUi() },
            input = input,
            isLoading = isLoading
        )
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), ChatUiState())

    private fun isLocalMessageSameAsServer(local: ChatMessage, server: ChatMessage): Boolean {
        return local.senderId == server.senderId && local.content == server.content
    }

    init {
        observeMessages()
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.InputChanged -> {
                _input.value = action.text
            }
            ChatAction.Send -> sendMessage()
            ChatAction.OpenAttachment -> { /* Future */ }
            ChatAction.OpenEmoji -> { /* Future */ }
            is ChatAction.Retry -> { /* Future */ }
            ChatAction.Dismiss -> { /* Handled by UI */ }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeMessagesUseCase(roomId).collectLatest { domainMessages ->
                _observedMessages.value = domainMessages
                _isLoading.value = false
                _events.send(ChatEvent.ScrollToBottom)
            }
        }
    }

    private fun sendMessage() {
        val content = _input.value
        if (content.isBlank()) return

        val trimmed = content.trim()
        _input.value = ""

        val userId = connectionRepository.currentUserId ?: ""
        val tempId = "local_${UuidUtils.randomUuid()}"
        val localMessage = ChatMessage(
            id = tempId,
            senderId = userId,
            senderName = "",
            senderAvatar = null,
            content = ChatContent.Text(trimmed),
            timestamp = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()),
            status = MessageStatus.Sending,
            isMine = true
        )
        _localMessages.value = _localMessages.value + localMessage

        viewModelScope.launch {
            sendMessageUseCase(roomId, userId, trimmed)
                .onSuccess {
                    _localMessages.value = _localMessages.value.map {
                        if (it.id == tempId) it.copy(status = MessageStatus.Sent) else it
                    }
                }
                .onFailure { error ->
                    _localMessages.value = _localMessages.value.map {
                        if (it.id == tempId) it.copy(status = MessageStatus.Failed(error.name)) else it
                    }
                    _events.send(ChatEvent.ShowError("Failed to send: ${error.name}"))
                }
        }
    }

    fun retryFailedMessage(messageId: String) {
        val failedMessage = _localMessages.value.find { it.id == messageId }
        if (failedMessage == null || failedMessage.status !is MessageStatus.Failed) return

        viewModelScope.launch {
            val content = when (failedMessage.content) {
                is ChatContent.Text -> failedMessage.content.body
                else -> return@launch
            }
            val userId = connectionRepository.currentUserId ?: ""

            sendMessageUseCase(roomId, userId, content)
                .onSuccess {
                    _localMessages.value = _localMessages.value.map {
                        if (it.id == messageId) it.copy(status = MessageStatus.Sending) else it
                    }
                }
                .onFailure { error ->
                    _localMessages.value = _localMessages.value.map {
                        if (it.id == messageId) it.copy(status = MessageStatus.Failed(error.name)) else it
                    }
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