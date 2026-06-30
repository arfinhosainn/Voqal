package app.voqal.com.feature.room.domain

enum class RoomConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    FAILED
}