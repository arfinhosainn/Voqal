package app.voqal.com.feature.onboarding.presentation.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingAuthError
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailViewModel(
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingAuthDataSource: OnboardingAuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(
        EmailState(email = onboardingDraftStore.email)
    )
    val state = _state.asStateFlow()

    private val _events = Channel<EmailEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            onboardingDraftStore.draft.collectLatest { draft ->
                if (_state.value.email.isBlank() && draft.email.isNotBlank()) {
                    _state.update { it.copy(email = draft.email, error = null) }
                }
            }
        }
    }

    fun onAction(action: EmailAction) {
        when (action) {
            is EmailAction.OnEmailChange -> {
                val email = action.email.trim()
                if (email != onboardingDraftStore.email) {
                    onboardingDraftStore.otpCode = List(6) { null }
                }
                onboardingDraftStore.updateEmail(email)
                _state.update {
                    it.copy(
                        email = email,
                        error = null
                    )
                }
            }
            EmailAction.OnContinueClick -> submitEmail()
        }
    }

    private fun submitEmail() {
        val email = _state.value.email
        if (!_state.value.isFormValid || _state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }

            when (val result = onboardingAuthDataSource.sendEmailOtp(email)) {
                is Result.Success -> {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(EmailEvent.NavigateToNext)
                }
                is Result.Failure -> {
                    val message = result.error.toEmailErrorMessage()
                    _state.update { it.copy(error = message, isSubmitting = false) }
                    _events.send(EmailEvent.ShowSnackbar(message))
                }
            }
        }
    }

    private fun OnboardingAuthError.toEmailErrorMessage(): String {
        return when (this) {
            OnboardingAuthError.NotConfigured -> "Supabase is not configured yet"
            OnboardingAuthError.InvalidEmail -> "Email is incorrect"
            OnboardingAuthError.Network -> "Check your connection and try again"
            OnboardingAuthError.TooManyRequests -> "Too many attempts. Try again later"
            OnboardingAuthError.InvalidOtp,
            OnboardingAuthError.Unknown -> "Could not send verification code"
        }
    }
}
