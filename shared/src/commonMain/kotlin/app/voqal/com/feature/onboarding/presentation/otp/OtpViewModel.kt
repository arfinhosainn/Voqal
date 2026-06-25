package app.voqal.com.feature.onboarding.presentation.otp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingAuthDataSource
import app.voqal.com.feature.onboarding.domain.OnboardingAuthError
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ResendCountdownSeconds = 30

class OtpViewModel(
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingAuthDataSource: OnboardingAuthDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(
        OtpState(
            code = onboardingDraftStore.otpCode,
            emailAddress = onboardingDraftStore.email
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<OtpEvent>()
    val events = _events.receiveAsFlow()

    private var currentlyFocusedIndex: Int = 0
    private var resendCountdownJob: Job? = null
    private var verifyOtpJob: Job? = null

    init {
        viewModelScope.launch {
            onboardingDraftStore.draft.collectLatest { draft ->
                if (_state.value.emailAddress.isBlank() && draft.email.isNotBlank()) {
                    _state.update { it.copy(emailAddress = draft.email) }
                }
            }
        }
        startResendCountdown()
    }

    fun onAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnEnterNumber -> handleNumberInput(action.number, action.index)
            is OtpAction.OnChangeFieldFocused -> {
                currentlyFocusedIndex = action.index
            }
            OtpAction.OnKeyboardBack -> handleBackspaceTraversal()
            OtpAction.OnResendCodeClick -> resendOtpCode()
            OtpAction.OnChangeEmailClick -> changeEmail()
            OtpAction.OnVerifyClick -> verifyOtpCode()
        }
    }

    private fun handleNumberInput(number: Int?, index: Int) {
        verifyOtpJob?.cancel()
        _state.update { currentState ->
            val updatedCode = currentState.code.toMutableList().apply {
                this[index] = number
            }
            onboardingDraftStore.otpCode = updatedCode
            currentState.copy(
                code = updatedCode,
                error = null,
                verificationStatus = OtpVerificationStatus.Idle,
                isSubmitting = false
            )
        }

        if (_state.value.isValid && !_state.value.isSubmitting) {
            verifyOtpCode()
            return
        }

        viewModelScope.launch {
            if (number != null && index < 5) {
                // Number entered: Shift focus forward
                _events.send(OtpEvent.FocusField(index + 1))
            } else if (number == null && index > 0) {
                // Field cleared directly: Shift focus backward
                _events.send(OtpEvent.FocusField(index - 1))
            }
        }
    }

    private fun handleBackspaceTraversal() {
        // Triggers when backspace is pressed on an already empty field
        if (currentlyFocusedIndex > 0) {
            viewModelScope.launch {
                _events.send(OtpEvent.FocusField(currentlyFocusedIndex - 1))
            }
        }
    }

    private fun resendOtpCode() {
        if (_state.value.resendSecondsRemaining > 0) return

        viewModelScope.launch {
            val email = _state.value.emailAddress
            if (email.isBlank()) {
                _events.send(OtpEvent.ShowSnackbar("Enter your email again"))
                _events.send(OtpEvent.NavigateToEmail)
                return@launch
            }

            _state.update { it.copy(isSubmitting = true, error = null) }
            when (val result = onboardingAuthDataSource.sendEmailOtp(email)) {
                is Result.Failure -> {
                    val message = result.error.toOtpErrorMessage()
                    _state.update { it.copy(error = message, isSubmitting = false) }
                    _events.send(OtpEvent.ShowSnackbar(message))
                    return@launch
                }
                is Result.Success -> Unit
            }

            onboardingDraftStore.otpCode = List(6) { null }
            _state.update {
                it.copy(
                    code = onboardingDraftStore.otpCode,
                    error = null,
                    resendSecondsRemaining = ResendCountdownSeconds,
                    verificationStatus = OtpVerificationStatus.Idle,
                    isSubmitting = false
                )
            }
            startResendCountdown()
            _events.send(OtpEvent.ShowSnackbar("A new code has been sent!"))
        }
    }

    private fun changeEmail() {
        viewModelScope.launch {
            _events.send(OtpEvent.NavigateToEmail)
        }
    }

    private fun verifyOtpCode() {
        val currentState = state.value
        if (!currentState.isValid || currentState.isSubmitting) return
        val email = currentState.emailAddress
        val token = currentState.codeString
        if (email.isBlank()) {
            viewModelScope.launch {
                _events.send(OtpEvent.ShowSnackbar("Enter your email again"))
                _events.send(OtpEvent.NavigateToEmail)
            }
            return
        }

        verifyOtpJob?.cancel()
        verifyOtpJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true,
                    error = null,
                    verificationStatus = OtpVerificationStatus.Checking
                )
            }

            when (val result = onboardingAuthDataSource.verifyEmailOtp(email, token)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            error = null,
                            verificationStatus = OtpVerificationStatus.Valid
                        )
                    }
                    delay(350)
                    _events.send(OtpEvent.NavigateToNext)
                }
                is Result.Failure -> {
                    val message = result.error.toOtpErrorMessage()
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            error = message,
                            verificationStatus = OtpVerificationStatus.Invalid
                        )
                    }
                    _events.send(OtpEvent.ShowSnackbar(message))
                }
            }
        }
    }

    private fun startResendCountdown() {
        resendCountdownJob?.cancel()
        resendCountdownJob = viewModelScope.launch {
            while (_state.value.resendSecondsRemaining > 0) {
                delay(1000)
                _state.update {
                    it.copy(resendSecondsRemaining = (it.resendSecondsRemaining - 1).coerceAtLeast(0))
                }
            }
        }
    }

    private fun OnboardingAuthError.toOtpErrorMessage(): String {
        return when (this) {
            OnboardingAuthError.NotConfigured -> "Supabase is not configured yet"
            OnboardingAuthError.InvalidOtp -> "Verification code is incorrect"
            OnboardingAuthError.InvalidEmail -> "Email is incorrect"
            OnboardingAuthError.Network -> "Check your connection and try again"
            OnboardingAuthError.TooManyRequests -> "Too many attempts. Try again later"
            OnboardingAuthError.Unknown -> "Could not verify code"
        }
    }
}
