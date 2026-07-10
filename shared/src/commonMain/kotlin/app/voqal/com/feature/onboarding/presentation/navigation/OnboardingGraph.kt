package app.voqal.com.feature.onboarding.presentation.navigation


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import app.voqal.com.core.presentation.util.ImagePicker
import app.voqal.com.feature.onboarding.presentation.email.EmailRoot
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameRoot
import app.voqal.com.feature.onboarding.presentation.interest.ChooseInterestsRoot
import app.voqal.com.feature.onboarding.presentation.language.ChooseLanguageRoot
import app.voqal.com.feature.onboarding.presentation.password.PasswordRoot
import app.voqal.com.feature.onboarding.presentation.photo.AddPhotoRoot
import app.voqal.com.feature.onboarding.presentation.username.PickUsernameRoot
import app.voqal.com.feature.permission.presentation.PermissionRoot
import app.voqal.com.core.permissions.domain.PermissionType
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingGraph

private const val OnboardingTransitionDurationMillis = 1000

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavController,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    imagePicker: ImagePicker
) {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(OnboardingTransitionDurationMillis)
        ) + fadeIn(animationSpec = tween(OnboardingTransitionDurationMillis))
    }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(OnboardingTransitionDurationMillis)
        ) + fadeOut(animationSpec = tween(OnboardingTransitionDurationMillis))
    }
    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(OnboardingTransitionDurationMillis)
        ) + fadeIn(animationSpec = tween(OnboardingTransitionDurationMillis))
    }
    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(OnboardingTransitionDurationMillis)
        ) + fadeOut(animationSpec = tween(OnboardingTransitionDurationMillis))
    }

    navigation<OnboardingGraph>(
        startDestination = OnboardingRoute.Email
    ) {
        // 1. Email Entry Screen
        composable<OnboardingRoute.Email>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            EmailRoot(
                onNavigateToNext = { isNewUser -> 
                    navController.navigate(OnboardingRoute.Password(isNewUser = isNewUser)) 
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 2. Password Entry Screen
        composable<OnboardingRoute.Password>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<OnboardingRoute.Password>()
            PasswordRoot(
                isNewUser = route.isNewUser,
                onNavigateToNext = { step ->
                    if (step != null && step >= 7) {
                        onOnboardingComplete()
                    } else {
                        navController.navigate(OnboardingRoute.FullName)
                    }
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 3. Full Name Entry Screen
        composable<OnboardingRoute.FullName>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            FullNameRoot(
                onNavigate = { navController.navigate(OnboardingRoute.Username) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 4. Username Entry Screen
        composable<OnboardingRoute.Username>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            PickUsernameRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.AddPhoto) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 5. Profile Photo Selection Screen
        composable<OnboardingRoute.AddPhoto>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            AddPhotoRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.ChooseLanguage) },
                onBack = { navController.popBackStack() },
                imagePicker = imagePicker,
                modifier = modifier
            )
        }

        // 6. Preferred Language Selection Screen
        composable<OnboardingRoute.ChooseLanguage>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            ChooseLanguageRoot(
                onNavigateToNext = { chosenLanguage ->
                    navController.navigate(OnboardingRoute.LocationPermission)
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 7. Location Permission Screen
        composable<OnboardingRoute.LocationPermission>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            PermissionRoot(
                permissionType = PermissionType.LOCATION,
                emoji = "📍",
                title = "share your location to discover local rooms",
                description = "",
                onPermissionHandled = {
                    navController.navigate(OnboardingRoute.MicrophonePermission)
                },
                modifier = modifier
            )
        }

        // 8. Microphone Permission Screen
        composable<OnboardingRoute.MicrophonePermission>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            PermissionRoot(
                permissionType = PermissionType.MICROPHONE,
                emoji = "🎙️",
                title = "turn on your mic to join the conversation",
                description = "",
                onPermissionHandled = {
                    navController.navigate(OnboardingRoute.ChooseInterests)
                },
                modifier = modifier
            )
        }

        // 9. User Interests Chip Selection Screen
        composable<OnboardingRoute.ChooseInterests>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            ChooseInterestsRoot(
                onNavigateToNext = { selectedInterestIds ->
                    onOnboardingComplete()
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}
