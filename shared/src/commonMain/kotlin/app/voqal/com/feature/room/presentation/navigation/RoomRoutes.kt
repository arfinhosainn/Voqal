package app.voqal.com.feature.room.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object RoomGraph

sealed interface RoomRoute {
    @Serializable
    data object Rooms : RoomRoute
}
