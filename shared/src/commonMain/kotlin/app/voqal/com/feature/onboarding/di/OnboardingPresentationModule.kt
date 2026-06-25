package app.voqal.com.feature.onboarding.di

import app.voqal.com.feature.onboarding.presentation.email.EmailViewModel
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameViewModel
import app.voqal.com.feature.onboarding.presentation.interest.ChooseInterestsViewModel
import app.voqal.com.feature.onboarding.presentation.language.LanguageViewModel
import app.voqal.com.feature.onboarding.presentation.otp.OtpViewModel
import app.voqal.com.feature.onboarding.presentation.photo.AddPhotoViewModel
import app.voqal.com.feature.onboarding.presentation.username.UsernameViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingPresentationModule = module {
    viewModelOf(::EmailViewModel)
    viewModelOf(::FullNameViewModel)
    viewModelOf(::UsernameViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::AddPhotoViewModel)
    viewModelOf(::LanguageViewModel)
    viewModelOf(::ChooseInterestsViewModel)
}
