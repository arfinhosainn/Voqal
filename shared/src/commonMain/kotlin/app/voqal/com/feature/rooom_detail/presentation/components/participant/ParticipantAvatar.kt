package app.voqal.com.feature.rooom_detail.presentation.components.participant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.core.presentation.util.CountryFlagResources
import app.voqal.com.feature.rooom_detail.presentation.model.MicState
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState

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
                Box {
                    // Avatar
                    if (state.avatar != null) {
                        Image(
                            painter = state.avatar,
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
                }
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
