package app.voqal.com.feature.chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme

import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.sp

@Composable
fun ChatInputBar(
    modifier: Modifier = Modifier,
    message: String,
    onMessageChange: (String) -> Unit,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit,
    onEmojiClick: () -> Unit = {},
    enabled: Boolean = true,
    placeholder: String = "Write a message..."
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AttachButton(
                onClick = onAttachClick
            )

            Spacer(Modifier.width(12.dp))

            // Message Input Field (Center)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(22.dp),
                color = VoqalTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(start = 18.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = message,
                        onValueChange = onMessageChange,
                        enabled = enabled,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = VoqalTheme.colors.onBackground
                        ),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (message.isEmpty()) {
                                Text(
                                    placeholder,
                                    color = VoqalTheme.colors.onSurface.copy(alpha = .55f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    IconButton(
                        onClick = onEmojiClick,
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.InsertEmoticon,
                            contentDescription = "Emoji",
                            tint = VoqalTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Send Button (Outside)
            Surface(
                modifier = Modifier.size(45.dp),
                onClick = onSendClick,
                enabled = enabled && message.isNotBlank(),
                shape = CircleShape,
                color = if (message.isNotBlank()) VoqalTheme.colors.primary else VoqalTheme.colors.surface
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (message.isNotBlank()) VoqalTheme.colors.onPrimary else VoqalTheme.colors.onSurface.copy(
                            alpha = 0.38f
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachButton(
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier.size(35.dp),
        shape = CircleShape,
        color = VoqalTheme.colors.surface,
        onClick = onClick
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Attach"
            )
        }
    }
}

@Preview
@Composable
private fun ChatInputBarPreview() {

    VoqalTheme {

        var text by remember {
            mutableStateOf("")
        }

        ChatInputBar(
            message = text,
            onMessageChange = {
                text = it
            },
            onAttachClick = {},
            onSendClick = {}
        )
    }
}
