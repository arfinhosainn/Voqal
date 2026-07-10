package app.voqal.com.feature.rooom_detail.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class RoomDetailRoute(val roomId: String, val asHost: Boolean = false)
