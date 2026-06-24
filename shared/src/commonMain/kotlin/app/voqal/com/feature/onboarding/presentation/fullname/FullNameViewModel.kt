package app.voqal.com.feature.onboarding.presentation.fullname


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FullNameViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        FullNameState(
            firstName = savedStateHandle["firstName"] ?: "",
            lastName = savedStateHandle["lastName"] ?: ""
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<FullNameEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: FullNameAction) {
        when (action) {
            is FullNameAction.OnFirstNameChange -> {
                savedStateHandle["firstName"] = action.value
                _state.update { it.copy(firstName = action.value) }
            }
            is FullNameAction.OnLastNameChange -> {
                savedStateHandle["lastName"] = action.value
                _state.update { it.copy(lastName = action.value) }
            }
            FullNameAction.OnContinueClick -> {
                if (_state.value.isFormValid) {
                    viewModelScope.launch {
                        _events.send(FullNameEvent.Navigate)
                    }
                }
            }
        }
    }
}