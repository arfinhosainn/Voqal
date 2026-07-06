package app.voqal.com.feature.rooom_detail.presentation.components



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.presentation.components.HomeTopBar
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_search
import voqal.shared.generated.resources.ic_vert

@Composable
fun RoomDetailTopBar(
    greeting: String,
    name: String,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
    onAvatarClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMinimizeClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VoqalTheme.colors.background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = greeting,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = VoqalTheme.extendedColors.mutedText
            )
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = VoqalTheme.colors.onBackground
            )
        }

        Spacer(Modifier.weight(1f))


            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Minimize",
                tint = VoqalTheme.colors.onBackground,
                modifier = Modifier.size(32.dp).clickable { onMinimizeClick() }
            )

    }
}

@PreviewLightDark
@Composable
private fun RoomDetailTopBarPreview() {
    VoqalTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VoqalTheme.colors.background)
        ) {
            RoomDetailTopBar(
                greeting = "Good morning",
                name = "Marian Marsh"
            )
        }
    }
}