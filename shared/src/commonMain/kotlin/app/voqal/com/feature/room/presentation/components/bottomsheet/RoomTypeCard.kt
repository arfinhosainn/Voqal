package app.voqal.com.feature.room.presentation.components.bottomsheet

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun RoomTypeCard(
    title: String,
    image: Painter,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        if (selected)
            VoqalTheme.extendedColors.roomBlue
        else
            Color.Transparent,
        label = "RoomTypeCardBackground"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = title,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
