package app.voqal.com.feature.onboarding.presentation.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PasswordViewModel : ViewModel() {

    private val _state = MutableStateFlow(PasswordState())
    val state = _state.asStateFlow()

    private val _events = Channel<PasswordEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: PasswordAction) {
        when (action) {
            is PasswordAction.OnPasswordChange -> {
                _state.update { it.copy(password = action.password, error = null) }
            }
            PasswordAction.OnContinueClick -> submitPassword()
            PasswordAction.OnForgotPasswordClick -> {
                // TODO: Implement forgot password
            }
            PasswordAction.OnMagicLinkClick -> {
                // TODO: Implement magic link
            }
        }
    }

    private fun submitPassword() {
        if (!_state.value.isFormValid || _state.value.isSubmitting) return

        viewModelScope.launch {
            _events.send(PasswordEvent.NavigateToNext)
        }
    }
}
