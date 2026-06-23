package app.voqal.com.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val VoqalMint = Color(0xFF96C09F)
val VoqalMintLight = Color(0xFFB8E1C1)
val VoqalInk = Color(0xFF080808)
val VoqalLightBackground = Color(0xFFFFFEFC)
val VoqalDarkBackground = Color(0xFF222222)

private val LightRoomBlue = Color(0xFFE4F7FB)
private val LightRoomCream = Color(0xFFFFF5E4)
private val LightRoomSlate = Color(0xFFF0F4FA)
private val LightEventPeach = Color(0xFFFFEDDE)
private val LightChip = Color(0xFFF2F2F2)
private val LightSearch = Color(0xFFF6F6F6)
private val LightDivider = Color(0xFFEAF1F5)

private val DarkRoomBlue = Color(0xFF334E53)
private val DarkRoomOlive = Color(0xFF6C624A)
private val DarkRoomSlate = Color(0xFF3D4656)
private val DarkEventBrown = Color(0xFF6A5545)
private val DarkChip = Color(0xFF3B3B3B)
private val DarkSearch = Color(0xFF454545)
private val DarkDivider = Color(0xFF72868F)

@Immutable
data class VoqalExtendedColors(
    val roomBlue: Color,
    val roomWarm: Color,
    val roomSlate: Color,
    val eventWarm: Color,
    val navigationSelected: Color,
    val chip: Color,
    val searchField: Color,
    val dottedDivider: Color,
    val mutedText: Color,
    val avatarAccent: Color,
)

val LightVoqalExtendedColors = VoqalExtendedColors(
    roomBlue = LightRoomBlue,
    roomWarm = LightRoomCream,
    roomSlate = LightRoomSlate,
    eventWarm = LightEventPeach,
    navigationSelected = VoqalMint,
    chip = LightChip,
    searchField = LightSearch,
    dottedDivider = LightDivider,
    mutedText = Color(0xFF8F9099),
    avatarAccent = Color(0xFFFF8A76),
)

val DarkVoqalExtendedColors = VoqalExtendedColors(
    roomBlue = DarkRoomBlue,
    roomWarm = DarkRoomOlive,
    roomSlate = DarkRoomSlate,
    eventWarm = DarkEventBrown,
    navigationSelected = VoqalMintLight,
    chip = DarkChip,
    searchField = DarkSearch,
    dottedDivider = DarkDivider,
    mutedText = Color(0xFFB9BBC2),
    avatarAccent = Color(0xFFFF8A76),
)
