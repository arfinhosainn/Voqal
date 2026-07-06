package app.voqal.com.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingProfileDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SplashViewModel(
    private val profileDataSource: OnboardingProfileDataSource
) : ViewModel() {

    private val _events = Channel<SplashEvent>()
    val events = _events.receiveAsFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            delay(1500.milliseconds) // Professional feel
            println("Splash: Checking session...")
            when (val result = profileDataSource.getOnboardingStep()) {
                is Result.Success -> {
                    val step = result.data
                    println("Splash: Onboarding step is $step")
                    // If step is 7, onboarding is complete
                    if (step != null && step >= 7) {
                        _events.send(SplashEvent.Authenticated)
                    } else {
                        println("Splash: Step is $step, redirecting to onboarding")
                        _events.send(SplashEvent.NotAuthenticated)
                    }
                }
                is Result.Failure -> {
                    println("Splash: Check failed with error ${result.error}")
                    _events.send(SplashEvent.NotAuthenticated)
                }
            }
        }
    }
}

sealed interface SplashEvent {
    data object Authenticated : SplashEvent
    data object NotAuthenticated : SplashEvent
}
