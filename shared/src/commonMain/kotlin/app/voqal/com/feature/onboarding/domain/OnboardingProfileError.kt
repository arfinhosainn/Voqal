package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.Error

enum class OnboardingProfileError : Error {
    NotConfigured,
    NotAuthenticated,
    UsernameTaken,
    Network,
    Unknown
}
