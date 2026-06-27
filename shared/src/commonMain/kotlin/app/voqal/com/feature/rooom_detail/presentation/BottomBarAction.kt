package app.voqal.com.feature.rooom_detail.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class BottomBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)