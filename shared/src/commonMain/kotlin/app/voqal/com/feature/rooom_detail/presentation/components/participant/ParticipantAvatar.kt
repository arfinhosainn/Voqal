package app.voqal.com.feature.rooom_detail.presentation.components.participant

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.CountryFlagResources
import app.voqal.com.feature.rooom_detail.presentation.model.MicState
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_raisehand

@Composable
fun ParticipantAvatar(
    state: ParticipantAvatarUiState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .then(
                    if (onClick != null) Modifier.clickable { onClick() } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            SpeakingRing(isSpeaking = state.isSpeaking) {
                ParticipantAvatarContent(state = state)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = state.name,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = VoqalTheme.colors.onBackground
        )
    }
}

@Composable
private fun ParticipantAvatarContent(
    state: ParticipantAvatarUiState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(90.dp)) {
        // Avatar
        if (state.avatarUrl != null) {
            AsyncImage(
                model = state.avatarUrl,
                contentDescription = state.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(VoqalTheme.shapes.extraLarge),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(VoqalTheme.shapes.extraLarge)
                    .background(VoqalTheme.colors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.name.firstOrNull()?.toString() ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    color = VoqalTheme.colors.primary
                )
            }
        }

        // Country Badge (Bottom Start)
        CountryBadge(
            flag = CountryFlagResources.resolve(state.countryCode),
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // Mic Badge (Bottom End)
        if (state.micState != MicState.ON) {
            MicBadge(
                micState = state.micState,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        // Hand Raised Indicator
        if (state.isHandRaised) {
            val scale = remember { Animatable(0f) }

            LaunchedEffect(state.handRaisedTimestamp) {
                scale.snapTo(0f)
                scale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                scale.animateTo(1f)
            }

            Image(
                painter = painterResource(Res.drawable.ic_raisehand),
                contentDescription = "Hand Raised",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(42.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
            )
        }
    }
}
