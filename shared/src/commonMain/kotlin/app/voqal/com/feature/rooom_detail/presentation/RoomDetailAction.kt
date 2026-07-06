package app.voqal.com.feature.rooom_detail.presentation

sealed interface RoomDetailAction {
    data object OnLeaveClick : RoomDetailAction
    data object OnEndClick : RoomDetailAction
    data object OnMicClick : RoomDetailAction
    data object OnHandClick : RoomDetailAction
    data object OnMinimizeClick : RoomDetailAction
    data object OnExpandClick : RoomDetailAction
    data object OnToggleEndRoomDialog : RoomDetailAction
}
