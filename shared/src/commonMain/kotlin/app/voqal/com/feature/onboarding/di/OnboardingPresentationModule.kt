package app.voqal.com.feature.onboarding.di

import app.voqal.com.core.data.createOnboardingDraftDataStore
import app.voqal.com.core.data.SupabaseClientFactory
import app.voqal.com.core.data.SupabaseConfig
import app.voqal.com.core.data.VoqalSupabaseConfig
import app.voqal.com.feature.onboarding.data.DataStoreOnboardingDraftDataSource
import app.voqal.com.feature.onboarding.data.SupabaseOnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingDraftLocalDataSource
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import app.voqal.com.feature.onboarding.presentation.email.EmailViewModel
import app.voqal.com.feature.onboarding.presentation.fullname.FullNameViewModel
import app.voqal.com.feature.onboarding.presentation.interest.ChooseInterestsViewModel
import app.voqal.com.feature.onboarding.presentation.language.LanguageViewModel
import app.voqal.com.feature.onboarding.presentation.otp.OtpViewModel
import app.voqal.com.feature.onboarding.presentation.photo.AddPhotoViewModel
import app.voqal.com.feature.onboarding.presentation.username.UsernameViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingPresentationModule = module {
    single { createOnboardingDraftDataStore() }
    single<SupabaseConfig> { VoqalSupabaseConfig.current }
    single<SupabaseClient> { SupabaseClientFactory.create(get()) }
    singleOf(::DataStoreOnboardingDraftDataSource) { bind<OnboardingDraftLocalDataSource>() }
    singleOf(::SupabaseOnboardingAuthDataSource) { bind<OnboardingAuthDataSource>() }
    singleOf(::OnboardingDraftStore)
    viewModel { EmailViewModel(get(), get()) }
    viewModel { FullNameViewModel(get(), get()) }
    viewModel { UsernameViewModel(get()) }
    viewModel { OtpViewModel(get(), get()) }
    viewModel { AddPhotoViewModel(get(), get()) }
    viewModel { LanguageViewModel(get()) }
    viewModel { ChooseInterestsViewModel(get()) }
}
