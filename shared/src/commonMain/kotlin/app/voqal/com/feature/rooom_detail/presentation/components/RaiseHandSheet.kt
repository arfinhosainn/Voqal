package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.components.VoqalPrimaryButton
import app.voqal.com.core.designsystem.theme.VoqalTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_hand
import voqal.shared.generated.resources.ic_raisehand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseHandSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRaiseHand: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = VoqalTheme.colors.surface,
            dragHandle = null,
            shape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp
            )
        ) {
            RaiseHandBottomSheet(
                onRaiseHand = onRaiseHand,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
fun RaiseHandBottomSheet(
    onRaiseHand: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_raisehand),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Raise your hand?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = VoqalTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "This will let the speakers know you have\nsomething you'd like to say",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = VoqalTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        VoqalPrimaryButton(
            text = "Raise hand",
            onClick = onRaiseHand,
            shape = VoqalTheme.shapes.medium
        )
        Spacer(Modifier.height(28.dp))

        TextButton(
            onClick = onDismiss
        ) {
            Text(
                text = "NEVER MIND",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                color = VoqalTheme.extendedColors.mutedText
            )
        }

        Spacer(Modifier.navigationBarsPadding())
    }
}

@PreviewLightDark
@Composable
fun PreviewRaiseHandBottomSheet() {
    VoqalTheme {
        RaiseHandBottomSheet(
            onRaiseHand = {},
            onDismiss = {}
        )
    }
}
