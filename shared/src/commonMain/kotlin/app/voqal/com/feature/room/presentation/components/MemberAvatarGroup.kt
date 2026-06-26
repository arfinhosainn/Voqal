package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import app.voqal.com.feature.room.domain.ParticipantUi

@Composable
fun MemberAvatarGroup(
    participants: List<ParticipantUi>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 6,
    avatarSize: Dp = 40.dp,
    overlap: Dp = 16.dp,
) {

    val visible = participants.take(maxVisible)

    Box(
        modifier = modifier
            .width(
                avatarSize +
                        (visible.size - 1) * (avatarSize - overlap)
            )
            .height(avatarSize)
    ) {

        visible.forEachIndexed { index, user ->

            Box(
                modifier = Modifier
                    .offset(
                        x = index * (avatarSize - overlap)
                    )
            ) {

                Avatar(user, avatarSize)
            }
        }
    }
}