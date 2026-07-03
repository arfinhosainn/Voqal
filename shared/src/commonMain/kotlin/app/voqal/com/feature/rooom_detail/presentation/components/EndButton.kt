package app.voqal.com.feature.rooom_detail.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun EndButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.height(55.dp),
        onClick = onClick,
        shape = VoqalTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF4B4B).copy(alpha = 0.2f)
        ),
        contentPadding = PaddingValues(horizontal = 5.dp)
    ) {
        Text(
            text = "🛑 End Room",
            color = Color(0xFFFF4B4B),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            fontFamily = VoqalTheme.typography.labelMedium.fontFamily
        )
    }
}
