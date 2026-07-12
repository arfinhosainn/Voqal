package app.voqal.com.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.rooom_detail.presentation.components.CircleIconButton

@Composable
fun ProfileTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "",
            modifier = Modifier.size(54.dp),
        )
        Text(
            text = "Profile",
            color = VoqalTheme.colors.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
        CircleIconButton(
            icon = Icons.Outlined.Settings,
            contentDescription = "Settings",
            onClick = onSettingsClick,
            size = 44.dp,
            iconSize = 22.dp,
        )
    }
}
