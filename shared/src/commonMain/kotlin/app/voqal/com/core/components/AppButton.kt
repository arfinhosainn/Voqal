package app.voqal.com.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun VoqalPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    shape: Shape = VoqalTheme.shapes.large,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = VoqalTheme.colors.primary,
        contentColor = VoqalTheme.colors.onPrimary,
        disabledContainerColor = VoqalTheme.colors.surfaceVariant,
        disabledContentColor = VoqalTheme.colors.onSurface.copy(alpha = 0.38f)
    ),
    contentPadding: PaddingValues = PaddingValues(horizontal = 35.dp, vertical = 15.dp),
    textStyle: TextStyle = VoqalTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
) {
    Button(
        onClick = onClick,

        modifier = modifier.wrapContentWidth(),
        enabled = enabled && !loading,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = VoqalTheme.colors.onPrimary,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                leadingIcon()
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = if (loading) "Please wait" else text,
            style = textStyle,
            color = VoqalTheme.colors.background,
            maxLines = 1
        )

        if (trailingIcon != null && !loading) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                trailingIcon()
            }
        }
    }
}

@Preview(showSystemUi = false)
@Composable
private fun VoqalPrimaryButtonPreview() {
    VoqalTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            VoqalPrimaryButton(text = "Let's Go", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            VoqalPrimaryButton(text = "Looks good", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            VoqalPrimaryButton(text = "Send", onClick = {}, enabled = false)
            Spacer(modifier = Modifier.height(12.dp))
            VoqalPrimaryButton(text = "Join the room in progress", onClick = {}, loading = false)
            Spacer(modifier = Modifier.height(16.dp))
            VoqalPrimaryButton(
                text = "Join the room in progress",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


