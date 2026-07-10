package app.voqal.com.feature.room.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.voqal.com.feature.room.presentation.RoomRoot

fun NavGraphBuilder.roomGraph(
    onNavigateToRoom: (roomId: String, asHost: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation<RoomGraph>(
        startDestination = RoomRoute.Rooms
    ) {
        composable<RoomRoute.Rooms> {
            RoomRoot(
                onRoomCreated = { roomId ->
                    onNavigateToRoom(roomId, true)
                },
                onRoomClick = { roomId ->
                    onNavigateToRoom(roomId, false)
                },
                modifier = modifier
            )
        }
    }
}
