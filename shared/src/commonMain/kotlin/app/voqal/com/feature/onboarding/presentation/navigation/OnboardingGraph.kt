package app.voqal.com.feature.onboarding.presentation.navigation


import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

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
    modifier: Modifier = Modifier
) {
    navigation<OnboardingGraph>(
        startDestination = OnboardingRoute.OtpVerification
    ) {
        // 1. Full Name Entry Screen
        composable<OnboardingRoute.FullName> {
            // Replace with your actual FullNameRoot implementation block
            // e.g., FullNameRoot(
            //     onNavigateToNext = { navController.navigate(OnboardingRoute.OtpVerification) },
            //     onBack = { navController.popBackStack() }
            // )
        }

        // 2. OTP Verification Screen
        composable<OnboardingRoute.OtpVerification> {
            OtpRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.AddPhoto) },
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        // 3. Profile Photo Selection Screen
        composable<OnboardingRoute.AddPhoto> {
            AddPhotoRoot(
                onNavigateToNext = { navController.navigate(OnboardingRoute.ChooseLanguage) },
                onBack = { navController.popBackStack() },
                onTriggerPhotoPicker = {
                    // Launch native/system picker interface channels here
                },
                modifier = modifier
            )
        }

        // 4. Preferred Language Selection Screen
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

        // 5. User Interests Chip Selection Screen
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