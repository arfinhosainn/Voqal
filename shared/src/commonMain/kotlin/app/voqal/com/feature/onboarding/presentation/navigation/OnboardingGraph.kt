package app.voqal.com.feature.onboarding.presentation.navigation


import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.voqal.com.core.presentation.util.ImagePicker

import app.voqal.com.feature.onboarding.presentation.email.EmailRoot
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameRoot
import app.voqal.com.feature.onboarding.presentation.username.PickUsernameRoot
import app.voqal.com.feature.onboarding.presentation.photo.AddPhotoRoot
import app.voqal.com.feature.onboarding.presentation.otp.OtpRoot
import app.voqal.com.feature.onboarding.presentation.language.ChooseLanguageRoot
import app.voqal.com.feature.onboarding.presentation.interest.ChooseInterestsRoot
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingGraph

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavController,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    imagePicker: ImagePicker
) {
    navigation<OnboardingGraph>(
        startDestination = OnboardingRoute.Email
    ) {
        // 1. Email Entry Screen
        composable<OnboardingRoute.Email> {
            EmailRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.OtpVerification) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 2. OTP Verification Screen
        composable<OnboardingRoute.OtpVerification> {
            OtpRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.FullName) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 3. Full Name Entry Screen
        composable<OnboardingRoute.FullName> {
            FullNameRoot(
                onNavigate = { navController.navigate(OnboardingRoute.Username) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 4. Username Entry Screen
        composable<OnboardingRoute.Username> {
            PickUsernameRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.AddPhoto) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 5. Profile Photo Selection Screen
        composable<OnboardingRoute.AddPhoto> {
            AddPhotoRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.ChooseLanguage) },
                onBack = { navController.popBackStack() },
                imagePicker = imagePicker,
                modifier = modifier
            )
        }

        // 6. Preferred Language Selection Screen
        composable<OnboardingRoute.ChooseLanguage> {
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
        composable<OnboardingRoute.ChooseInterests> {
            ChooseInterestsRoot(
                onNavigateToNext = { selectedInterestIds ->
                    // Onboarding flow concludes here, exit graph layout limits safely
                    onOnboardingComplete()
                },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }
    }
}
