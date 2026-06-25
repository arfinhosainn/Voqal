package app.voqal.com.feature.onboarding.presentation.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailViewModel : ViewModel() {

    private val _state = MutableStateFlow(EmailState())
    val state = _state.asStateFlow()

    private val _events = Channel<EmailEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: EmailAction) {
        when (action) {
            is EmailAction.OnEmailChange -> {
                _state.update {
                    it.copy(
                        email = action.email.trim(),
                        error = null
                    )
                }
            }
            EmailAction.OnContinueClick -> submitEmail()
        }
    }

    private fun submitEmail() {
        if (!_state.value.isFormValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                _events.send(EmailEvent.NavigateToNext)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isSubmitting = false) }
                _events.send(EmailEvent.ShowSnackbar(e.message ?: "An error occurred"))
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
