package app.voqal.com.feature.room.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter

@Immutable
data class ParticipantUi(
    val id: String,
    val name: String,
    val avatar: Painter? = null,
)