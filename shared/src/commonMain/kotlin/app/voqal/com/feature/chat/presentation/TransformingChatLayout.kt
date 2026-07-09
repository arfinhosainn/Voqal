package app.voqal.com.feature.chat.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import app.voqal.com.feature.chat.presentation.components.ChatInputBar
import app.voqal.com.feature.chat.presentation.components.ChatMessages
import app.voqal.com.feature.chat.presentation.components.ChatTopBar
import app.voqal.com.feature.chat.presentation.model.TransformingSheetTransition
import app.voqal.com.feature.rooom_detail.presentation.BottomBarAction
import app.voqal.com.feature.rooom_detail.presentation.RoomBottomBarContent

@Composable
fun TransformingChatLayout(
    transition: TransformingSheetTransition,
    chatState: ChatUiState,
    onChatEvent: (ChatEvent) -> Unit,
    roomActions: List<BottomBarAction>,
    isHost: Boolean,
    onLeaveRoom: () -> Unit,
    messagesListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {

        // 1. Room Bottom Bar Content (Sinking/Fading)
        if (transition.roomBar.alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = transition.roomBar.alpha
                        translationY = transition.roomBar.translationY.toPx()
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                RoomBottomBarContent(
                    actions = roomActions,
                    isHost = isHost,
                    onLeave = onLeaveRoom
                )
            }
        }

        // 2. Chat Header (Rising/Fading)
        if (transition.header.alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = 1f
                        translationY = 0f
                    }
            ) {
                ChatTopBar(
                    onCollapseClick = { onChatEvent(ChatEvent.Dismiss) }
                )
            }
        }

        // 3. Chat Messages (Settling/Fading)
        if (transition.messages.alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp, bottom = 80.dp) // Leave space for Header and Input
                    .graphicsLayer {
                        alpha = transition.messages.alpha
                        translationY = transition.messages.translationY.toPx()
                    }
            ) {
                ChatMessages(
                    messages = chatState.messages,
                    listState = messagesListState,
                    isPeek = transition.progress < 0.7f
                )
            }
        }

        // 4. Chat Input Bar (Rising/Fading)
        if (transition.input.alpha > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        alpha = transition.input.alpha
                        translationY = transition.input.translationY.toPx()
                    }
            ) {
                ChatInputBar(
                    message = chatState.input,
                    onMessageChange = { onChatEvent(ChatEvent.InputChanged(it)) },
                    onAttachClick = { onChatEvent(ChatEvent.OpenAttachment) },
                    onSendClick = { onChatEvent(ChatEvent.Send) },
                    onEmojiClick = { onChatEvent(ChatEvent.OpenEmoji) },
                    enabled = !chatState.isSending
                )
            }
        }
    }
}
