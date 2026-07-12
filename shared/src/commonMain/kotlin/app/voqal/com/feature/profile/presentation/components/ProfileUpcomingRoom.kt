package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.profile.presentation.model.UpcomingRoomUi

@Composable
fun ProfileUpcomingRoom(
    room: UpcomingRoomUi,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = VoqalTheme.shapes.small,
        color = VoqalTheme.extendedColors.chip,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = room.title,
                color = VoqalTheme.colors.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = room.timeDescription,
                color = VoqalTheme.extendedColors.mutedText,
                fontSize = 14.sp,
            )
        }
    }
}
