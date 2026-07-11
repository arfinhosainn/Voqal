package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    iconSize: Dp = 25.dp,
    shape: Shape = VoqalTheme.shapes.extraLarge,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = VoqalTheme.colors.surface
    ),
    tint: androidx.compose.ui.graphics.Color = VoqalTheme.colors.onBackground,
) {
    FilledIconButton(
        modifier = modifier.size(size),
        onClick = onClick,
        shape = shape,
        colors = colors,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
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


