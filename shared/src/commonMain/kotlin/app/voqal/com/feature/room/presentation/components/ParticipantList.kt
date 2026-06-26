package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.domain.ParticipantUi

@Composable
fun ParticipantList(
    participants: List<ParticipantUi>,
    onClick: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        participants.forEach {

            Text(
                modifier = Modifier.clickable {
                    onClick(it.id)
                },
                text = "${it.name} 💬",
                color = VoqalTheme.colors.onBackground,
                fontSize = 14.sp
            )
        }
    }
}