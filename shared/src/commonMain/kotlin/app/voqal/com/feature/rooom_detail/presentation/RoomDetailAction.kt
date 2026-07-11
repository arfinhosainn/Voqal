package app.voqal.com.feature.rooom_detail.presentation

import app.voqal.com.feature.rooom_detail.presentation.components.UserProfileUi

sealed interface RoomDetailAction {
    data object OnLeaveClick : RoomDetailAction
    data object OnEndClick : RoomDetailAction
    data object OnMicClick : RoomDetailAction
    data object OnHandClick : RoomDetailAction
    data object OnMinimizeClick : RoomDetailAction
    data object OnExpandClick : RoomDetailAction
    
    data object OnShowEndRoomDialog : RoomDetailAction
    data object OnDismissEndRoomDialog : RoomDetailAction
    
    data object OnShowRaiseHandSheet : RoomDetailAction
    data object OnDismissRaiseHandSheet : RoomDetailAction
    data object OnConfirmRaiseHand : RoomDetailAction

    data object OnChatClick : RoomDetailAction
    data object OnShowChatSheet : RoomDetailAction
    data object OnDismissChatSheet : RoomDetailAction

    data class OnParticipantClick(val participant: UserProfileUi) : RoomDetailAction
    data object OnDismissProfileSheet : RoomDetailAction
}
