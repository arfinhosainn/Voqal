package app.voqal.com.feature.onboarding.presentation.username


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.domain.Result
import app.voqal.com.feature.onboarding.domain.OnboardingProfileDataSource
import app.voqal.com.feature.onboarding.domain.toUserMessage
import app.voqal.com.feature.onboarding.presentation.OnboardingDraftStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsernameViewModel(
    private val onboardingDraftStore: OnboardingDraftStore,
    private val onboardingProfileDataSource: OnboardingProfileDataSource
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
        if (!state.value.isFormValid || state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }

            // Still check availability for UX, but don't call updateUsername
            when (val result = onboardingProfileDataSource.isUsernameAvailable(state.value.username)) {
                is Result.Success -> {
                    if (result.data) {
                        _state.update { it.copy(isSubmitting = false) }
                        _events.send(UsernameEvent.NavigateToNext)
                    } else {
                        _state.update { it.copy(isSubmitting = false, error = "Username taken") }
                    }
                }
                is Result.Error -> {
                    val message = result.error.toUserMessage()
                    _state.update { it.copy(error = message, isSubmitting = false) }
                    _events.send(UsernameEvent.ShowSnackbar(message))
                }
            }
        }
    }
}
