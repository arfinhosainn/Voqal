package app.voqal.com.feature.onboarding.presentation.otp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val _events = Channel<OtpEvent>()
    val events = _events.receiveAsFlow()

    private var currentlyFocusedIndex: Int = 0

    fun onAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnEnterNumber -> handleNumberInput(action.number, action.index)
            is OtpAction.OnChangeFieldFocused -> {
                currentlyFocusedIndex = action.index
            }
            OtpAction.OnKeyboardBack -> handleBackspaceTraversal()
            OtpAction.OnResendCodeClick -> resendOtpCode()
            OtpAction.OnVerifyClick -> verifyOtpCode()
        }
    }

    private fun handleNumberInput(number: Int?, index: Int) {
        _state.update { currentState ->
            val updatedCode = currentState.code.toMutableList().apply {
                this[index] = number
            }
            currentState.copy(code = updatedCode, error = null)
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
        viewModelScope.launch {
            _events.send(OtpEvent.ShowSnackbar("A new code has been sent!"))
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
}