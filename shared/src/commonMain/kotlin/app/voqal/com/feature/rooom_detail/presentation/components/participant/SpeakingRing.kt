package app.voqal.com.feature.rooom_detail.presentation.components.participant

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun SpeakingRing(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSpeaking) VoqalTheme.colors.primary else Color.Transparent,
        label = "SpeakingRingColor"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isSpeaking) 2.dp else 0.dp,
        label = "SpeakingRingWidth"
    )

    Box(
        modifier = modifier
            .border(width = borderWidth, color = borderColor, shape = VoqalTheme.shapes.extraLarge)
            .padding(0.dp)
    ) {
        content()
    }
}
