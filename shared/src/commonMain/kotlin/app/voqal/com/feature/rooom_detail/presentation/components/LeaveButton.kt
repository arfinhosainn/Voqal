package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun LeaveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        modifier = modifier.height(55.dp),
        onClick = onClick,
        shape = VoqalTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) Color(0xFF573A46) else Color(0xFFFFE3EF)
        ),
        contentPadding = PaddingValues(horizontal = 5.dp)
    ) {

        Text(
            text = "\uD83D\uDE05 Leave room",
            color = if (isSystemInDarkTheme()) Color(0xFFFF79B3) else Color(0xFFFF006D),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            fontFamily = VoqalTheme.typography.labelMedium.fontFamily
        )
    }
}


@PreviewLightDark
@Composable
fun PreviewLeaveButton() {
    VoqalTheme {
        LeaveButton {
            // Handle button click for preview
        }
    }
}
