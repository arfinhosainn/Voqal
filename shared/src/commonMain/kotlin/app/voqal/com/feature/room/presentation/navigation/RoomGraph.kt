package app.voqal.com.feature.room.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingRoute
import app.voqal.com.feature.room.presentation.RoomRoot

fun NavGraphBuilder.roomGraph(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    navigation<RoomGraph>(
        startDestination = RoomRoute.Rooms
    ) {
        composable<RoomRoute.Rooms> {
            RoomRoot(
                onRoomCreated = { roomId ->
                    navController.navigate(OnboardingRoute.RoomDetailRoute(roomId = roomId, asHost = true))
                },
                onRoomClick = { roomId ->
                    navController.navigate(OnboardingRoute.RoomDetailRoute(roomId = roomId, asHost = false))
                },
                modifier = modifier
            )
        }
    }
}
