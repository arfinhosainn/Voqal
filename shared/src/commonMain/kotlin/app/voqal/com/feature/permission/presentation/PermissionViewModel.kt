package app.voqal.com.feature.permission.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.voqal.com.core.permissions.domain.PermissionManager
import app.voqal.com.core.permissions.domain.PermissionType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val permissionManager: PermissionManager,
    private val permissionType: PermissionType,
    emoji: String,
    title: String,
    description: String
) : ViewModel() {

    private val _state = MutableStateFlow(
        PermissionScreenState(
            emoji = emoji,
            title = title,
            description = description,
            permissionType = permissionType
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<PermissionScreenEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: PermissionScreenAction) {
        when (action) {
            PermissionScreenAction.OnRequestClick -> requestPermission()
        }
    }

    private fun requestPermission() {
        viewModelScope.launch {
            _state.update { it.copy(isRequesting = true) }
            val result = permissionManager.request(permissionType)
            _events.send(PermissionScreenEvent.PermissionHandled(result))
            _state.update { it.copy(isRequesting = false) }
        }
    }
}
