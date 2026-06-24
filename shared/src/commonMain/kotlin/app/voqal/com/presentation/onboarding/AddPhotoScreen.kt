package app.voqal.com.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_edit
import voqal.shared.generated.resources.img
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AddPhotoScreen(
    onEditClick: () -> Unit,
    onBack: () -> Unit,
    onContinue: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var preview by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        OnboardingScaffold(
            onBack = onBack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Add a photo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VoqalTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Select a profile photo for your account",
                    fontSize = 14.sp,
                    color = VoqalTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(48.dp))

                ProfilePhotoPicker(
                    onEditClick = onEditClick,
                    onPreviewChanged = { preview = it }
                )

                Spacer(modifier = Modifier.weight(1f))

                VoqalPrimaryButton(
                    text = "Let's Go",
                    onClick = { /* TODO: Use onContinue properties here if needed */ },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        // Preview overlay sits completely on top of everything (Scaffold included)
        ProfilePhotoPreview(preview)
    }
}

@Composable
private fun ProfilePhotoPicker(
    onEditClick: () -> Unit,
    onPreviewChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 180.dp
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val strokeColor = VoqalTheme.colors.surface
    var zoom by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (zoom) 1.08f else 1f,
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Box(
        modifier = modifier.size(avatarSize).graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Box(
            Modifier.fillMaxSize()
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            val job = scope.launch {
                                delay(300.milliseconds)
                                zoom = true
                                onPreviewChanged(true)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            try {
                                awaitRelease()
                            } finally {
                                job.cancel()
                                zoom = false
                                onPreviewChanged(false)
                            }
                        }
                    )
                }
        ) {
            Image(
                painter = painterResource(Res.drawable.img),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(52.dp)
                .shadow(0.dp, CircleShape)
                .clip(CircleShape)
                .border(4.dp, strokeColor, CircleShape)
                .background(if (isSystemInDarkTheme()) VoqalTheme.extendedColors.chip else Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_edit),
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun ProfilePhotoPreview(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = .92f),
        exit = fadeOut() + scaleOut(targetScale = .92f)
    ) {
        val scale by animateFloatAsState(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessVeryLow
            ),
            label = ""
        )

        Box(
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = .82f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.img),
                contentDescription = null,
                modifier = Modifier
                    .size(340.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}