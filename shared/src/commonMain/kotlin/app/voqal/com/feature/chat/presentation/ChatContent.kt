package app.voqal.com.feature.chat.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.chat.presentation.components.ChatInputBar
import app.voqal.com.feature.chat.presentation.components.ChatMessages
import app.voqal.com.feature.chat.presentation.components.ChatTopBar

@Composable
fun ChatContent(
    state: ChatUiState,
    onEvent: (ChatEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty() &&
            listState.firstVisibleItemIndex <= 2
        ) {
            listState.animateScrollToItem(0)
        }
    }

    Surface(
        modifier = modifier,
        color = VoqalTheme.colors.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {

            ChatTopBar(
                onCollapseClick = {
                    onEvent(ChatEvent.Dismiss)
                }
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {

                ChatMessages(
                    modifier = Modifier.fillMaxSize(),
                    messages = state.messages,
                    listState = listState
                )

                // Future:
                // NewMessageIndicator()
            }

            ChatInputBar(
                message = state.input,
                onMessageChange = {
                    onEvent(ChatEvent.InputChanged(it))
                },
                onAttachClick = {
                    onEvent(ChatEvent.OpenAttachment)
                },
                onEmojiClick = {
                    onEvent(ChatEvent.OpenEmoji)
                },
                onSendClick = {
                    onEvent(ChatEvent.Send)
                },
                enabled = !state.isSending
            )
        }
    }
}