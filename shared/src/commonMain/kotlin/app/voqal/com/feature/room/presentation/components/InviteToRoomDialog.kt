package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.domain.InviteParticipantUi
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_copy
import voqal.shared.generated.resources.ic_more
import voqal.shared.generated.resources.ic_share

@Composable
fun InviteToRoomDialog(
    participants: List<InviteParticipantUi>,
    onDismissRequest: () -> Unit,
    onSearchQueryChange: (String) -> Unit = {},
    onParticipantClick: (String) -> Unit = {},
    onShareClick: () -> Unit = {},
    onDuplicateClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = VoqalTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = 560.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Invite someone into the room",
                    color = VoqalTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                InviteSearchField(
                    onQueryChange = onSearchQueryChange
                )

                Spacer(Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(participants, key = { it.id }) { participant ->
                        InviteParticipantItem(
                            participant = participant,
                            onClick = { onParticipantClick(participant.id) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DialogActionButton(
                        icon = vectorResource(Res.drawable.ic_share),
                        contentDescription = "Share invite link",
                        onClick = onShareClick,
                        modifier = Modifier.weight(1f)
                    )
                    DialogActionButton(
                        icon = vectorResource(Res.drawable.ic_copy),
                        contentDescription = "Copy invite link",
                        onClick = onDuplicateClick,
                        modifier = Modifier.weight(1f)
                    )
                    DialogActionButton(
                        icon = vectorResource(Res.drawable.ic_more),
                        contentDescription = "More options",
                        onClick = onMoreClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun InviteToRoomDialogPreview() {
    VoqalTheme {
        InviteToRoomDialog(
            participants = listOf(
                InviteParticipantUi(id = "1", name = "Alex", avatarUrl = null),
                InviteParticipantUi(id = "2", name = "Jordan", avatarUrl = null),
                InviteParticipantUi(id = "3", name = "Taylor", avatarUrl = null),
                InviteParticipantUi(id = "4", name = "Morgan", avatarUrl = null),
                InviteParticipantUi(id = "5", name = "Casey", avatarUrl = null),
            ),
            onDismissRequest = {},
            onSearchQueryChange = {},
            onParticipantClick = {},
            onShareClick = {},
            onDuplicateClick = {},
            onMoreClick = {}
        )
    }
}

@Composable
 fun DialogActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(VoqalTheme.extendedColors.chip)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = VoqalTheme.colors.onSurface
        )
    }
}
