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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        reverseLayout = true,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = messages,
            key = { _, message -> message.id }
        ) { index, message ->
            
            // Dynamic Grouping Logic
            val nextMessage = if (index > 0) messages[index - 1] else null
            val isNextFromSameSender = nextMessage?.senderId == message.senderId
            
            // Determine if we should show avatar and name
            // In reverseLayout, index 0 is the newest message (bottom)
            // Grouping logic: if the message ABOVE (higher index in reverse) is from same sender, hide avatar
            val previousMessage = if (index < messages.lastIndex) messages[index + 1] else null
            val isFirstInGroup = previousMessage?.senderId != message.senderId

            ChatBubble(
                message = message,
                showAvatar = isFirstInGroup,
                showSenderName = isFirstInGroup,
                modifier = Modifier.padding(
                    top = if (isFirstInGroup && index < messages.lastIndex) 8.dp else 0.dp
                )
            )
        }
    }
}
