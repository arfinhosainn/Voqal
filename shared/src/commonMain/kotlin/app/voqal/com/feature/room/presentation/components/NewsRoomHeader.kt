package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
 fun NewsRoomHeader(
    category: String,
    title: String,
    onMoreClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.Top
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = category,
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = title,
                color = VoqalTheme.colors.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        IconButton(onClick = onMoreClick) {

            Icon(
                Icons.Filled.MoreHoriz,
                null,
                tint = VoqalTheme.colors.onBackground
            )
        }
    }
}