package app.voqal.com.feature.chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.voqal.com.core.designsystem.theme.VoqalTheme

@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    onCollapseClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Chat",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = VoqalTheme.colors.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onCollapseClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Collapse",
                    tint = VoqalTheme.colors.onBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = VoqalTheme.colors.outline.copy(alpha = 0.15f)
        )
    }
}

@Preview
@Composable
private fun ChatTopBarPreview() {
    VoqalTheme {
        Surface(color = VoqalTheme.colors.surface) {
            ChatTopBar(
                onCollapseClick = {}
            )
        }
    }
}
