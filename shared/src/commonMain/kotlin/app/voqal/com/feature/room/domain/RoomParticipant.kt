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
    val isAudioEnabled: Boolean
)

fun RoomParticipant.toParticipantAvatarUiState(): ParticipantAvatarUiState = ParticipantAvatarUiState(
    id = sessionId,
    name = name,
    avatar = null,
    countryFlag = null, // backfill once you wire your own profile lookup — see note above
    micState = if (isAudioEnabled) MicState.ON else MicState.OFF,
    isSpeaking = isSpeaking
)
