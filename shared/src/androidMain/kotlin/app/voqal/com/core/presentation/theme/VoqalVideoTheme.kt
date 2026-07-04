package app.voqal.com.core.presentation.theme

import androidx.compose.runtime.Composable
import app.voqal.com.core.designsystem.theme.VoqalTheme
import io.getstream.video.android.compose.theme.StreamColors
import io.getstream.video.android.compose.theme.VideoTheme

@Composable
actual fun VoqalVideoTheme(
    content: @Composable () -> Unit
) {
    val voqalColors = VoqalTheme.colors
    
    val streamColors = StreamColors.defaultColors().copy(
        brandPrimary = voqalColors.primary,
        basePrimary = voqalColors.background,
        baseSecondary = voqalColors.surface,
        baseTertiary = voqalColors.surfaceVariant,
        baseQuaternary = voqalColors.outline,
        iconDefault = voqalColors.onSurface,
        iconActive = voqalColors.primary,
        brandRed = voqalColors.error
    )

    VideoTheme(
        colors = streamColors,
        content = content
    )
}
