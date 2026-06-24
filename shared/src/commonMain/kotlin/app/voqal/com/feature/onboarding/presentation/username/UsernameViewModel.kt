package app.voqal.com.feature.onboarding.presentation.username


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsernameViewModel : ViewModel() {

    private val _state = MutableStateFlow(UsernameState())
    val state = _state.asStateFlow()

    private val _events = Channel<UsernameEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: UsernameAction) {
        when (action) {
            is UsernameAction.OnUsernameChange -> {
                _state.update {
                    // Sanitize username input: lowercase, remove spaces/special chars if needed
                    it.copy(
                        username = action.username.lowercase().replace("\\s".toRegex(), ""),
                        error = null
                    )
                }
            }
            is UsernameAction.OnContinueClick -> {
                submitUsername()
            }
        }
    }

    private fun submitUsername() {
        if (!state.value.isFormValid) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                _events.send(UsernameEvent.NavigateToNext)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isSubmitting = false) }
                _events.send(UsernameEvent.ShowSnackbar(e.message ?: "An error occurred"))
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}