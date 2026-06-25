package app.voqal.com.feature.onboarding.domain

import app.voqal.com.core.domain.Error

enum class OnboardingAuthError : Error {
    NotConfigured,
    InvalidEmail,
    InvalidOtp,
    Network,
    TooManyRequests,
    Unknown
}

