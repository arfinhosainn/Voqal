package app.voqal.com.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.chat.presentation.model.ChatMessageUi
import app.voqal.com.feature.chat.domain.model.ChatContent
import app.voqal.com.feature.chat.domain.model.MessageStatus
import coil3.compose.AsyncImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    message: ChatMessageUi,
    showAvatar: Boolean = true,
    showSenderName: Boolean = true,
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        },
        verticalAlignment = Alignment.Bottom
    ) {

        if (!message.isMine) {
            if (showAvatar) {
                AvatarPlaceholder(message.senderAvatar, message.senderName)
                Spacer(Modifier.width(10.dp))
            } else {
                Spacer(Modifier.width(50.dp))
            }
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isMine) {
                Alignment.End
            } else {
                Alignment.Start
            }
        ) {

            if (!message.isMine && showSenderName) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelMedium,
                    color = VoqalTheme.colors.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (message.isMine)
                    VoqalTheme.colors.primary
                else
                    VoqalTheme.colors.surface,
                modifier = Modifier.graphicsLayer {
                    alpha = if (message.status == MessageStatus.Sending) 0.6f else 1f
                }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    when (val content = message.content) {
                        is ChatContent.Text -> {
                            Text(
                                text = content.body,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = if (message.isMine)
                                    VoqalTheme.colors.onPrimary
                                else
                                    VoqalTheme.colors.onSurface
                            )
                        }
                        is ChatContent.Image -> { /* Future */ }
                    }

                    val time = message.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                    val hour = time.hour.toString().padStart(2, '0')
                    val minute = time.minute.toString().padStart(2, '0')
                    
                    Text(
                        text = "$hour:$minute",
                        style = MaterialTheme.typography.labelSmall,
                        color = (if (message.isMine)
                            VoqalTheme.colors.onPrimary
                        else
                            VoqalTheme.colors.onSurface).copy(alpha = 0.6f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarPlaceholder(
    url: String?,
    name: String
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(VoqalTheme.colors.primary.copy(alpha = .25f)),
        contentAlignment = Alignment.Center
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.take(1).uppercase(),
                color = VoqalTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun ChatBubblePreview() {
    VoqalTheme {
        Surface(color = VoqalTheme.colors.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ChatBubble(
                    message = ChatMessageUi(
                        id = "1",
                        senderId = "user1",
                        senderName = "Alice",
                        senderAvatar = null,
                        content = ChatContent.Text("Hey everyone 👋"),
                        timestamp = kotlin.time.Clock.System.now(),
                        status = MessageStatus.Sent,
                        isMine = false
                    )
                )

                ChatBubble(
                    message = ChatMessageUi(
                        id = "2",
                        senderId = "me",
                        senderName = "Me",
                        senderAvatar = null,
                        content = ChatContent.Text("Hi Alice! Welcome to the room."),
                        timestamp = kotlin.time.Clock.System.now(),
                        status = MessageStatus.Sent,
                        isMine = true
                    )
                )

                ChatBubble(
                    message = ChatMessageUi(
                        id = "3",
                        senderId = "user1",
                        senderName = "Alice",
                        senderAvatar = null,
                        content = ChatContent.Text("This is a longer message to see how the bubble behaves when the content spans multiple lines. It should wrap naturally without becoming too wide."),
                        timestamp = kotlin.time.Clock.System.now(),
                        status = MessageStatus.Sent,
                        isMine = false
                    )
                )
            }
        }
    }
}
