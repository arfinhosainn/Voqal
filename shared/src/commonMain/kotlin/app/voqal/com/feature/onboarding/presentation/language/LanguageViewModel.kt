package app.voqal.com.feature.onboarding.presentation.language


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageViewModel : ViewModel() {

    private val _state = MutableStateFlow(LanguageState())
    val state = _state.asStateFlow()

    private val _events = Channel<LanguageEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: LanguageAction) {
        when (action) {
            is LanguageAction.OnLanguageSelect -> {
                _state.update { it.copy(selectedLanguage = action.language) }
            }
            LanguageAction.OnContinueClick -> {
                submitSelectedLanguage()
            }
        }
    }

    private fun submitSelectedLanguage() {
        val selection = state.value.selectedLanguage ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                // Future Hook: Save selection to local preferences store here
                _events.send(LanguageEvent.NavigateToNext(selection))
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false) }
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}