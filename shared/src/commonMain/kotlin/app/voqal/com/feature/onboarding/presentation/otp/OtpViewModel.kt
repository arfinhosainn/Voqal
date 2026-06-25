package app.voqal.com.feature.onboarding.presentation.otp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val onboardingDraftStore: OnboardingDraftStore
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
        _state.update { currentState ->
            val updatedCode = currentState.code.toMutableList().apply {
                this[index] = number
            }
            onboardingDraftStore.otpCode = updatedCode
            currentState.copy(code = updatedCode, error = null)
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
            onboardingDraftStore.otpCode = List(6) { null }
            _state.update {
                it.copy(
                    code = onboardingDraftStore.otpCode,
                    error = null,
                    resendSecondsRemaining = ResendCountdownSeconds
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
        if (!state.value.isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                // Execute verification network call with: state.value.codeString
                _events.send(OtpEvent.NavigateToNext)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isSubmitting = false) }
                _events.send(OtpEvent.ShowSnackbar(e.message ?: "Invalid code verification"))
            } finally {
                _state.update { it.copy(isSubmitting = false) }
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
}
