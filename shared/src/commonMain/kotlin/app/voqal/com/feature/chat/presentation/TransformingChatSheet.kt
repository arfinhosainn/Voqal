package app.voqal.com.feature.chat.presentation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.chat.presentation.model.MotionState
import app.voqal.com.feature.chat.presentation.model.TransformingSheetTransition

@Composable
fun TransformingChatSheet(
    progress: Float,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (TransformingSheetTransition) -> Unit
) {
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val fullHeight = maxHeight
        val currentSheetHeightDp = fullHeight * (0.88f * progress + 0.12f) // Placeholder for actual height mapping

        val transition = calculateTransition(progress, currentSheetHeightDp)

        Surface(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = transition.cornerRadius,
                topEnd = transition.cornerRadius
            ),
            color = lerpColor(
                VoqalTheme.colors.background,
                VoqalTheme.extendedColors.chip,
                progress
            ),
            shadowElevation = transition.elevation,
            tonalElevation = 0.dp
        ) {
            content(transition)
        }
    }
}

private fun calculateTransition(
    progress: Float,
    sheetHeight: Dp
): TransformingSheetTransition {
    return TransformingSheetTransition(
        progress = progress,
        sheetHeight = sheetHeight,
        cornerRadius = lerp(30.dp, 40.dp, progress),
        elevation = 0.dp,
        
        roomBar = MotionState(
            alpha = (1f - progress.progressBetween(0f, 0.3f)),
            translationY = lerp(0.dp, 24.dp, progress.progressBetween(0f, 0.3f))
        ),
        
        header = MotionState(
            alpha = progress.progressBetween(0.2f, 0.5f),
            translationY = lerp(24.dp, 0.dp, progress.progressBetween(0.2f, 0.5f))
        ),
        
        messages = MotionState(
            alpha = progress.progressBetween(0.45f, 0.8f),
            translationY = lerp(30.dp, 0.dp, progress.progressBetween(0.45f, 0.8f))
        ),
        
        input = MotionState(
            alpha = progress.progressBetween(0.7f, 1.0f),
            translationY = lerp(40.dp, 0.dp, progress.progressBetween(0.7f, 1.0f))
        ),
        
        dragHandle = MotionState(
            alpha = progress.progressBetween(0.1f, 0.4f)
        )
    )
}

private fun Float.progressBetween(start: Float, end: Float): Float {
    if (end == start) return 1f
    return ((this - start) / (end - start)).coerceIn(0f, 1f)
}
