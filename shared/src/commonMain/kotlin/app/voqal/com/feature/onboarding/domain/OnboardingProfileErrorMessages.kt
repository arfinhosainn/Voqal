package app.voqal.com.feature.onboarding.domain

fun OnboardingProfileError.toUserMessage(): String {
    return when (this) {
        OnboardingProfileError.NotConfigured -> "Supabase is not configured yet"
        OnboardingProfileError.NotAuthenticated -> "Sign in again to continue"
        OnboardingProfileError.UsernameTaken -> "That username is already taken"
        OnboardingProfileError.Network -> "Check your connection and try again"
        OnboardingProfileError.Unknown -> "Something went wrong. Please try again"
    }
}
