package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.util.NumberFormatter
import app.voqal.com.feature.profile.presentation.model.ProfileUi

@Composable
fun ProfileStats(
    profile: ProfileUi,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        StatCard(
            value = NumberFormatter.toShortCount(profile.followersCount),
            label = "Followers",
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = NumberFormatter.toShortCount(profile.followingCount),
            label = "Following",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun StatCard(
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
