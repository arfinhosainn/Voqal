package app.voqal.com.feature.onboarding.presentation.navigation



import kotlinx.serialization.Serializable

sealed interface OnboardingRoute {

    @Serializable
    data object FullName : OnboardingRoute

    @Serializable
    data object OtpVerification : OnboardingRoute

    @Serializable
    data object AddPhoto : OnboardingRoute

    @Serializable
    data object ChooseLanguage : OnboardingRoute

    @Serializable
    data object ChooseInterests : OnboardingRoute
}