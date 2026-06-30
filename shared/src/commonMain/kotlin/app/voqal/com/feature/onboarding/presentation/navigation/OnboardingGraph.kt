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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.voqal.com.core.presentation.util.ImagePicker

import app.voqal.com.feature.onboarding.presentation.email.EmailRoot
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameRoot
import app.voqal.com.feature.onboarding.presentation.username.PickUsernameRoot
import app.voqal.com.feature.onboarding.presentation.photo.AddPhotoRoot
import app.voqal.com.feature.onboarding.presentation.language.ChooseLanguageRoot
import app.voqal.com.feature.onboarding.presentation.interest.ChooseInterestsRoot
import app.voqal.com.feature.room.presentation.RoomRoot
import app.voqal.com.feature.room.presentation.navigation.RoomRoute
import app.voqal.com.feature.rooom_detail.presentation.RoomDetailRoot
import app.voqal.com.feature.rooom_detail.presentation.RoomDetailScreen
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
                onNavigateToNext = { navController.navigate(OnboardingRoute.FullName) },
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
                    // You can optionally pass args using type-safe route configurations if needed
                    navController.navigate(OnboardingRoute.ChooseInterests)
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 7. User Interests Chip Selection Screen
        composable<OnboardingRoute.ChooseInterests>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            ChooseInterestsRoot(
                onNavigateToNext = { selectedInterestIds ->
                    // Onboarding flow concludes here, exit graph layout limits safely
                    onOnboardingComplete()
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }


        composable<OnboardingRoute.RoomDetailRoute>(
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            RoomDetailRoot(
                onLeave = { navController.popBackStack() }
            )
        }
    }
}
