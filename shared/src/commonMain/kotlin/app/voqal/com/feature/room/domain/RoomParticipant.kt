package app.voqal.com.feature.room.domain

import app.voqal.com.feature.rooom_detail.presentation.model.MicState
import app.voqal.com.feature.rooom_detail.presentation.model.ParticipantAvatarUiState

data class RoomParticipant(
    val sessionId: String,
    val userId: String,
    val name: String,
    val imageUrl: String?,
    val role: String,
    val isSpeaking: Boolean,
    val isAudioEnabled: Boolean,
    val isHandRaised: Boolean = false,
    val handRaisedTimestamp: Long = 0L,
    val countryCode: String? = null
)

fun RoomParticipant.toParticipantAvatarUiState(): ParticipantAvatarUiState = ParticipantAvatarUiState(
    id = sessionId,
    name = name,
    avatarUrl = imageUrl,
    countryCode = countryCode,
    micState = if (isAudioEnabled) MicState.ON else MicState.OFF,
    isSpeaking = isSpeaking,
    isHandRaised = isHandRaised,
    handRaisedTimestamp = handRaisedTimestamp
)
