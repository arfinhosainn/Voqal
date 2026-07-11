package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.presentation.components.DialogActionButton
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.chinese
import voqal.shared.generated.resources.ic_expand
import voqal.shared.generated.resources.ic_more
import voqal.shared.generated.resources.ic_send

data class UserProfileUi(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String?,
    val followersCount: Int,
    val followingCount: Int,
    val bio: String,
    val isFollowing: Boolean = false,
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileSheet(
    isVisible: Boolean,
    profile: UserProfileUi,
    onDismiss: () -> Unit,
    onFollowClick: (UserProfileUi) -> Unit,
    onShareClick: (UserProfileUi) -> Unit,
    onTipClick: (UserProfileUi) -> Unit,
    onMoreClick: (UserProfileUi) -> Unit,
) {
    if (!isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = VoqalTheme.colors.surface,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 16.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = profile.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        color = VoqalTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profile.role,
                        color = VoqalTheme.extendedColors.mutedText,
                        fontSize = 14.sp,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { onFollowClick(profile) },
                    shape = VoqalTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoqalTheme.colors.primary,
                        contentColor = VoqalTheme.colors.onPrimary,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 14.dp
                    ),
                ) {
                    Text(
                        text = if (profile.isFollowing) "Following" else "Follow",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                StatCard(
                    value = "43",
                    label = "Followers",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "343",
                    label = "Following",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = profile.bio,
                color = VoqalTheme.extendedColors.mutedText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))



            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                DialogActionButton(
                    icon = vectorResource(Res.drawable.ic_send),
                    contentDescription = "Share profile",
                    onClick = { onShareClick(profile)},
                    modifier = Modifier.weight(1f),
                )
                DialogActionButton(
                    icon = vectorResource(Res.drawable.ic_expand),
                    contentDescription = "Send a tip",
                    onClick = { onTipClick(profile) },
                    modifier = Modifier.weight(1f),
                )
                DialogActionButton(
                    icon = vectorResource(Res.drawable.ic_more),
                    contentDescription = "More options",
                    onClick = { onMoreClick(profile) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(VoqalTheme.shapes.small)
            .background(VoqalTheme.extendedColors.chip)
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = value,
            color = VoqalTheme.colors.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = VoqalTheme.extendedColors.mutedText,
            fontSize = 14.sp,
        )
    }
}


