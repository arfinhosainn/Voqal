package app.voqal.com.feature.rooom_detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.rooom_detail.presentation.components.CircleIconButton
import app.voqal.com.feature.rooom_detail.presentation.components.LeaveButton
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_hand
import voqal.shared.generated.resources.ic_micoff
import voqal.shared.generated.resources.ic_more
import voqal.shared.generated.resources.ic_send

@Composable
fun RoomDetailBottomBar(
    modifier: Modifier = Modifier,
    actions: List<BottomBarAction>,
    isHost: Boolean = false,
    onLeave: () -> Unit,
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        color = VoqalTheme.colors.background
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            LeaveButton(
                modifier = modifier,
                onClick = onLeave
            )

            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { action ->
                    CircleIconButton(
                        icon = action.icon,
                        contentDescription = action.contentDescription,
                        onClick = action.onClick,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
fun PreviewRoomDetailBottomBar() {
    VoqalTheme {
        RoomDetailBottomBar(
            modifier = Modifier.fillMaxWidth(),
            onLeave = {},
            isHost = true,
            actions = listOf(
                BottomBarAction(
                    icon = vectorResource(Res.drawable.ic_send),
                    contentDescription = "Mic",
                    onClick = {}
                ),
                BottomBarAction(
                    icon = vectorResource(Res.drawable.ic_hand),
                    contentDescription = "Mic",
                    onClick = {}
                ),
                BottomBarAction(
                    icon = vectorResource(Res.drawable.ic_micoff),
                    contentDescription = "Mic",
                    onClick = {}
                )
            )

        )
    }
}