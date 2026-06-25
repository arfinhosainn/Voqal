package app.voqal.com.feature.onboarding.di

import app.voqal.com.core.data.createOnboardingDraftDataStore
import app.voqal.com.feature.onboarding.data.DataStoreOnboardingDraftDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingDraftLocalDataSource
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import app.voqal.com.feature.onboarding.presentation.email.EmailViewModel
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameViewModel
import app.voqal.com.feature.onboarding.presentation.interest.ChooseInterestsViewModel
import app.voqal.com.feature.onboarding.presentation.language.LanguageViewModel
import app.voqal.com.feature.onboarding.presentation.otp.OtpViewModel
import app.voqal.com.feature.onboarding.presentation.photo.AddPhotoViewModel
import app.voqal.com.feature.onboarding.presentation.username.UsernameViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module

val onboardingPresentationModule = module {
    single { createOnboardingDraftDataStore() }
    singleOf(::DataStoreOnboardingDraftDataSource) { bind<OnboardingDraftLocalDataSource>() }
    singleOf(::OnboardingDraftStore)
    viewModelOf(::EmailViewModel)
    viewModelOf(::FullNameViewModel)
    viewModelOf(::UsernameViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::AddPhotoViewModel)
    viewModelOf(::LanguageViewModel)
    viewModelOf(::ChooseInterestsViewModel)
}
