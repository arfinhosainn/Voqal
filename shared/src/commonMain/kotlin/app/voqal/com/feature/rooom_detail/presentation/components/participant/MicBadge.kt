package app.voqal.com.feature.rooom_detail.presentation.components.participant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.rooom_detail.presentation.model.MicState
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_send
import voqal.shared.generated.resources.ic_hand
import voqal.shared.generated.resources.ic_mic
import voqal.shared.generated.resources.ic_micoff

@Composable
fun MicBadge(
    micState: MicState,
    modifier: Modifier = Modifier
) {
    val icon = when (micState) {
        MicState.ON -> vectorResource(Res.drawable.ic_mic) // Fallback icons
        MicState.OFF -> vectorResource(Res.drawable.ic_micoff)
        MicState.MUTED -> vectorResource(Res.drawable.ic_hand)
    }

    val strokeColor = VoqalTheme.colors.surface


    Box(
        modifier = modifier
            .size(33.dp)
            .border(2.dp, strokeColor, CircleShape)
            .background(if (isSystemInDarkTheme())
                VoqalTheme.extendedColors.chip else Color(0xFFF0F0F0), CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Mic State",
            tint = VoqalTheme.colors.onBackground,
            modifier = Modifier.size(16.dp)
        )
    }
}
