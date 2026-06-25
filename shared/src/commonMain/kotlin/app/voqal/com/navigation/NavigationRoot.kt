package app.voqal.com.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.voqal.com.core.presentation.util.ImagePicker
import app.voqal.com.feature.onboarding.presentation.navigation.OnboardingGraph
import app.voqal.com.feature.onboarding.presentation.navigation.onboardingNavGraph
import kotlinx.serialization.Serializable

@Serializable
data object MainDashboardGraph

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    imagePicker: ImagePicker,
    startDestination: Any = OnboardingGraph // Or dynamically check if user is logged in
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingNavGraph(
            navController = navController,
            onOnboardingComplete = {
                // Clear onboarding off the backstack completely
                navController.navigate(MainDashboardGraph) {
                    popUpTo(OnboardingGraph) { inclusive = true }
                }
            },
            imagePicker = imagePicker
        )


    }
}