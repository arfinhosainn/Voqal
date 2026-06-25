package app.voqal.com.feature.onboarding.presentation.language


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

class LanguageViewModel(
    private val onboardingDraftStore: OnboardingDraftStore
) : ViewModel() {

    private val _state = MutableStateFlow(
        LanguageState().let { state ->
            state.copy(
                selectedLanguage = state.languages.firstOrNull {
                    it.id == onboardingDraftStore.selectedLanguageId
                }
            )
        }
    )
    val state = _state.asStateFlow()

    private val _events = Channel<LanguageEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            onboardingDraftStore.draft.collectLatest { draft ->
                if (_state.value.selectedLanguage == null && draft.selectedLanguageId != null) {
                    _state.update { state ->
                        state.copy(
                            selectedLanguage = state.languages.firstOrNull {
                                it.id == draft.selectedLanguageId
                            }
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: LanguageAction) {
        when (action) {
            is LanguageAction.OnLanguageSelect -> {
                onboardingDraftStore.updateSelectedLanguageId(action.language.id)
                _state.update { it.copy(selectedLanguage = action.language) }
            }
            is LanguageAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
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
