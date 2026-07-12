package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.profile.presentation.model.ProfileUi
import coil3.compose.AsyncImage

@Composable
fun ProfileHeader(
    profile: ProfileUi,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.size(72.dp)) {
            if (profile.avatarUrl != null) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = profile.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(VoqalTheme.extendedColors.chip),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = profile.name.firstOrNull()?.uppercase() ?: "?",
                        color = VoqalTheme.extendedColors.mutedText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

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
            onClick = onFollowClick,
            shape = VoqalTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = VoqalTheme.colors.primary,
                contentColor = VoqalTheme.colors.onPrimary,
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        ) {
            Text(
                text = if (profile.isFollowing) "Following" else "Follow",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = VoqalTheme.colors.background
            )
        }
    }
}
