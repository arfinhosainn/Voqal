package app.voqal.com.navigation

import app.voqal.com.core.components.VoqalBottomNavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class BottomNavStore {
    private val _selectedTab = MutableStateFlow(VoqalBottomNavTab.Home)
    val selectedTab = _selectedTab.asStateFlow()

    private val _isVisible = MutableStateFlow(false)
    val isVisible = _isVisible.asStateFlow()

    fun onTabClick(tab: VoqalBottomNavTab) {
        _selectedTab.value = tab
    }

    fun setVisible(visible: Boolean) {
        _isVisible.value = visible
    }

    private val _createRoomClicks = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val createRoomClicks = _createRoomClicks.asSharedFlow()

    fun onCreateRoomClick() {
        _createRoomClicks.tryEmit(Unit)
    }
}
