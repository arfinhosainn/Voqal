package app.voqal.com.feature.onboarding.presentation.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingAuthError
import app.voqal.com.feature.onboarding.domain.OnboardingProfileDataSource
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PasswordViewModel(
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingAuthDataSource: OnboardingAuthDataSource,
    private val onboardingProfileDataSource: OnboardingProfileDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(PasswordState())
    val state = _state.asStateFlow()

    private val _events = Channel<PasswordEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: PasswordAction, isNewUser: Boolean) {
        when (action) {
            is PasswordAction.OnPasswordChange -> {
                val password = action.password
                onboardingDraftStore.updatePassword(password)
                _state.update { it.copy(password = password, error = null) }
            }
            PasswordAction.OnContinueClick -> submitPassword(isNewUser)
            PasswordAction.OnForgotPasswordClick -> {
                // TODO: Implement forgot password
            }
            PasswordAction.OnMagicLinkClick -> {
                // TODO: Implement magic link
            }
        }
    }

    private fun submitPassword(isNewUser: Boolean) {
        if (!_state.value.isFormValid || _state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            
            val email = onboardingDraftStore.email
            val password = _state.value.password

            val result = if (isNewUser) {
                onboardingAuthDataSource.signUp(email, password)
            } else {
                onboardingAuthDataSource.signIn(email, password)
            }

            when (result) {
                is Result.Success -> {
                    // Critical: Ensure the profile (and email) is saved to the database IMMEDIATELY
                    // after successful sign-up/sign-in so that future checks find it.
                    val ensureResult = onboardingProfileDataSource.ensureProfileExists()
                    if (ensureResult is Result.Error) {
                        _state.update { 
                            it.copy(
                                isSubmitting = false,
                                error = "Profile Sync Error"
                            ) 
                        }
                        _events.send(PasswordEvent.ShowSnackbar("Sync Error: ${ensureResult.error}"))
                        return@launch
                    }

                    // Fetch the latest onboarding step for this user
                    val stepResult = onboardingProfileDataSource.getOnboardingStep()
                    val step = (stepResult as? Result.Success)?.data

                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(PasswordEvent.NavigateToNext(step))
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isSubmitting = false,
                            error = result.error.toPasswordErrorMessage()
                        ) 
                    }
                    _events.send(PasswordEvent.ShowSnackbar(result.error.toPasswordErrorMessage()))
                }
            }
        }
    }

    private fun OnboardingAuthError.toPasswordErrorMessage(): String {
        return when (this) {
            OnboardingAuthError.NotConfigured -> "Supabase is not configured yet"
            OnboardingAuthError.InvalidEmail -> "Email is incorrect"
            OnboardingAuthError.Network -> "Check your connection and try again"
            OnboardingAuthError.TooManyRequests -> "Too many attempts. Try again later"
            OnboardingAuthError.Unknown -> "Incorrect password"
        }
    }
}
