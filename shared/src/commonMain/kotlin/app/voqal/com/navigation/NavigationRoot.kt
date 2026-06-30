package app.voqal.com.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.voqal.com.core.presentation.util.ImagePicker
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingGraph
import app.voqal.com.feature.onboarding.presentation.navigation.onboardingNavGraph
import app.voqal.com.feature.room.presentation.navigation.RoomGraph
import app.voqal.com.feature.room.presentation.navigation.roomGraph
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    imagePicker: ImagePicker,
    startDestination: Any = OnboardingGraph
) {
    val navController = rememberNavController()
    val onboardingDraftStore = koinInject<OnboardingDraftStore>()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingNavGraph(
            navController = navController,
            onOnboardingComplete = {
                onboardingDraftStore.clear()
                // Clear onboarding off the backstack completely
                navController.navigate(RoomGraph) {
                    popUpTo(OnboardingGraph) { inclusive = true }
                }
            },
            imagePicker = imagePicker
        )

        roomGraph(
            navController = navController,
            modifier = modifier
        )

    }
}
