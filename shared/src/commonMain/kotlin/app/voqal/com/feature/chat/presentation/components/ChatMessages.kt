package app.voqal.com.feature.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.voqal.com.feature.chat.presentation.model.ChatMessageUi

@Composable
fun ChatMessages(
    messages: List<ChatMessageUi>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    isPeek: Boolean = false
) {
    val displayMessages = if (isPeek) messages.take(2) else messages

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        reverseLayout = true,
        contentPadding = PaddingValues(
            horizontal = 16.dp, 
            vertical = if (isPeek) 8.dp else 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = !isPeek
    ) {
        itemsIndexed(
            items = displayMessages,
            key = { _, message -> message.id }
        ) { index, message ->
            
            // Dynamic Grouping Logic
            val previousMessage = if (index < displayMessages.lastIndex) displayMessages[index + 1] else null
            val isFirstInGroup = previousMessage?.senderId != message.senderId

            ChatBubble(
                message = message,
                showAvatar = isFirstInGroup && !isPeek, // Hide avatars in peek for more space
                showSenderName = isFirstInGroup && !isPeek,
                modifier = Modifier.padding(
                    top = if (isFirstInGroup && index < displayMessages.lastIndex) 8.dp else 0.dp
                )
            )
        }
    }
}
