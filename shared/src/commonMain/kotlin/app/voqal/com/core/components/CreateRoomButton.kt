package app.voqal.com.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme

// Custom shape that mimics the Windows logo perspective
private val WindowsPerspectiveShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height

    // 1. Perspective Depth (How much shorter the left side is)
    // Adjusting the 0.15f multiplier makes the 3D slant more or less extreme.
    val leftInset = h * 0.06f
    val slope = leftInset / w

    // 2. Corner Radius
    // 0.25f scales perfectly with the width, matching your roughly 30.dp rounding.
    val cr = w * 0.28f

    // Start at Left Edge (just below the top-left corner)
    moveTo(0f, leftInset + cr)

    // Top-Left Corner
    quadraticTo(
        x1 = 0f, y1 = leftInset,             // Control Point (Theoretical sharp corner)
        x2 = cr, y2 = leftInset - slope * cr // End Point (Connecting to the slope)
    )

    // Top Edge (Slopes upwards to the right)
    lineTo(w - cr, slope * cr)

    // Top-Right Corner
    quadraticTo(
        x1 = w, y1 = 0f,
        x2 = w, y2 = cr
    )

    // Right Edge (Taller)
    lineTo(w, h - cr)

    // Bottom-Right Corner
    quadraticTo(
        x1 = w, y1 = h,
        x2 = w - cr, y2 = h - slope * cr
    )

    // Bottom Edge (Slopes upwards to the left)
    lineTo(cr, h - leftInset + slope * cr)

    // Bottom-Left Corner
    quadraticTo(
        x1 = 0f, y1 = h - leftInset,
        x2 = 0f, y2 = h - leftInset - cr
    )

    close()
}

@Composable
fun CreateRoomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 112.dp,
    height: Dp = 104.dp,
    color: Color = VoqalTheme.colors.background,
    contentDescription: String = "Create room"
) {
    Box(
        modifier = modifier
            .size(
                width = width,
                height = height
            )
            // Apply the new perspective shape here!
            .clip(WindowsPerspectiveShape)
            .background(Color(0xFF96C09F))
            .semantics {
                this.contentDescription = contentDescription
            }
            .clickable(
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(42.dp)
        ) {
            val strokeWidth = 3.dp.toPx()
            val center = size.width / 2f
            val lineStart = size.width * 0.22f
            val lineEnd = size.width * 0.78f

            drawLine(
                color = color,
                start = Offset(lineStart, center),
                end = Offset(lineEnd, center),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = color,
                start = Offset(center, lineStart),
                end = Offset(center, lineEnd),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun CreateRoomButtonPreview() {
    VoqalTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .size(160.dp, 184.dp),
            contentAlignment = Alignment.Center
        ) {
            CreateRoomButton(
                onClick = {}
            )
        }
    }
}

@PreviewLightDark()
@Composable
private fun CreateRoomButtonBottomNavSizePreview() {
    VoqalTheme {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            CreateRoomButton(
                onClick = {},
                width = 64.dp,
                height = 58.dp
            )
        }
    }
}