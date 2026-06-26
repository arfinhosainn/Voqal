package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
) {
    val dashedDividerColor = if (isSystemInDarkTheme()) Color(0xFFFFFFFF).copy(alpha = 0.20f) else Color(0xFF000000).copy(alpha = 0.20f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {

        val dash = 10.dp.toPx()
        val gap = 5.dp.toPx()

        var start = 0f

        while (start < size.width) {

            drawLine(
                color = dashedDividerColor,
                start = Offset(start, size.height / 2),
                end = Offset(
                    minOf(start + dash, size.width),
                    size.height / 2
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            start += dash + gap
        }
    }
}