package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.PreviewLightDark
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.domain.ParticipantUi
import app.voqal.com.feature.room.presentation.components.MemberAvatarGroup
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_hand
import voqal.shared.generated.resources.ic_more
import voqal.shared.generated.resources.ic_send

@Composable
fun MiniRoomBar(
    roomName: String,
    participantCount: Int,
    participants: List<ParticipantAvatarUiState>,
    modifier: Modifier = Modifier,
    onRoomClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onHandClick: () -> Unit = {},
    onExpandClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onRoomClick),
        color = Color(0xFF3D4351),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = roomName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MemberAvatarGroup(
                            participants = participants.map {
                                ParticipantUi(id = it.id, name = it.name, avatar = it.avatar)
                            },
                            avatarSize = 24.dp,
                            overlap = 8.dp,
                            maxVisible = 4,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = participantCount.toString(),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                MiniRoomAction(
                    icon = vectorResource(Res.drawable.ic_send),
                    onClick = onSendClick
                )

                Spacer(Modifier.width(12.dp))

                MiniRoomAction(
                    icon = vectorResource(Res.drawable.ic_hand),
                    onClick = onHandClick
                )

                Spacer(Modifier.width(12.dp))

                MiniRoomAction(
                    icon = vectorResource(Res.drawable.ic_more),
                    onClick = onExpandClick
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniRoomAction(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        color = Color(0xFF242730),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun MiniRoomBarPreview() {
    VoqalTheme {
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(16.dp)) {
            MiniRoomBar(
                roomName = "Kotlin Malaysia",
                participantCount = 23,
                participants = emptyList()
            )
        }

    }
}
