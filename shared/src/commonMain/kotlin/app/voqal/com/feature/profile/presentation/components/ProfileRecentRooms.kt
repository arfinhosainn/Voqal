package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun ProfileRecentRooms(
    rooms: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        rooms.forEach { room ->
            Text(
                text = "•  $room",
                color = VoqalTheme.extendedColors.mutedText,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
