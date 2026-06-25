package app.voqal.com.feature.onboarding.presentation.username


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsernameViewModel(
    private val onboardingDraftStore: OnboardingDraftStore
) : ViewModel() {

    private val _state = MutableStateFlow(
        UsernameState(username = onboardingDraftStore.username)
    )
    val state = _state.asStateFlow()

    private val _events = Channel<UsernameEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            onboardingDraftStore.draft.collectLatest { draft ->
                if (_state.value.username.isBlank() && draft.username.isNotBlank()) {
                    _state.update { it.copy(username = draft.username, error = null) }
                }
            }
        }
    }

    fun onAction(action: UsernameAction) {
        when (action) {
            is UsernameAction.OnUsernameChange -> {
                val username = action.username.lowercase().replace("\\s".toRegex(), "")
                onboardingDraftStore.updateUsername(username)
                _state.update {
                    it.copy(
                        username = username,
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
