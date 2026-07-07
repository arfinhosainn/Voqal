package app.voqal.com.feature.onboarding.presentation.navigation



import kotlinx.serialization.Serializable

sealed interface OnboardingRoute {

    @Serializable
    data object Email : OnboardingRoute

    @Serializable
    data class Password(val isNewUser: Boolean) : OnboardingRoute

    @Serializable
    data object FullName : OnboardingRoute

    @Serializable
    data object Username : OnboardingRoute

    @Serializable
    data object AddPhoto : OnboardingRoute

    @Serializable
    data object ChooseLanguage : OnboardingRoute

    @Serializable
    data object ChooseInterests : OnboardingRoute

    @Serializable
    data object LocationPermission : OnboardingRoute

    @Serializable
    data object MicrophonePermission : OnboardingRoute

    @Serializable
    data object RoomDetail : OnboardingRoute

    @Serializable
    data class RoomDetailRoute(val roomId: String, val asHost: Boolean = false)

}
