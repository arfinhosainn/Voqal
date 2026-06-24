package app.voqal.com.feature.onboarding.di

import app.voqal.com.feature.onboarding.presentation.fullname.FullNameViewModel
import app.voqal.com.feature.onboarding.presentation.otp.OtpViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingPresentationModule = module {
    viewModelOf(::FullNameViewModel)
    viewModelOf(::OtpViewModel)
}