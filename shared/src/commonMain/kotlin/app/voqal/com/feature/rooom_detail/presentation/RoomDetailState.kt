package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.core.presentation.util.UiText

data class RoomDetailState(
    val roomId: String = "",
    val title: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
)
