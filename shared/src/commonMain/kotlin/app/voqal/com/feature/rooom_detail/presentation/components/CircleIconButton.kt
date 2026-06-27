package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    FilledIconButton(
        modifier = modifier.size(50.dp),
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = VoqalTheme.extendedColors.chip
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = VoqalTheme.colors.onBackground,
            modifier = Modifier.size(22.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun CircleIconButtonPreview() {
    VoqalTheme {
        CircleIconButton(
            icon = Icons.Default.Add,
            contentDescription = "Add",
            onClick = {}
        )
    }
}


