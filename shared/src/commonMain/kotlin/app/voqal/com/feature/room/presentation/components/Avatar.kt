package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.CountryFlagResources
import app.voqal.com.feature.room.domain.ParticipantUi
import app.voqal.com.feature.rooom_detail.presentation.components.participant.CountryBadge
import coil3.compose.AsyncImage

@Composable
fun Avatar(
    participant: ParticipantUi,
    size: Dp
) {

    Box(
        modifier = Modifier
            .size(size)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    2.dp,
                    VoqalTheme.colors.background,
                    CircleShape
                )
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {

            if (participant.avatarUrl != null) {
                AsyncImage(
                    model = participant.avatarUrl,
                    contentDescription = participant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = participant.name.take(1).uppercase(),
                    color = Color.White,
                    style = VoqalTheme.typography.bodyLarge
                )
            }
        }

        // Country Badge (Proportional size)
        CountryBadge(
            flag = CountryFlagResources.resolve(participant.countryCode),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(size * 0.4f)
        )
    }
}
