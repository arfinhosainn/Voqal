package app.voqal.com.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val VoqalMint = Color(0xFF96C09F)
val VoqalMintLight = Color(0xFFB2DABB)
val VoqalInk = Color(0xFF000000)
val VoqalLightBackground = Color(0xFFFFFFFF)
val VoqalDarkBackground = Color(0xFF222222)

private val LightRoomBlue = Color(0xFFE6F5F9)
private val LightRoomCream = Color(0xFFFBF5E8)
private val LightRoomSlate = Color(0xFFEFF2F8)
private val LightEventPeach = Color(0xFFFFEFE2)
private val LightChip = Color(0xFFF4F4F4)
private val LightSearch = Color(0xFFF6F6F6) // not in provided swatch — unchanged
private val LightDivider = Color(0xFFEAF1F5) // not in provided swatch — unchanged

private val DarkRoomBlue = Color(0xFF35484D)
private val DarkRoomOlive = Color(0xFF645A47)
private val DarkRoomSlate = Color(0xFF39404D)
private val DarkEventBrown = Color(0xFF614E3E)
private val DarkChip = Color(0xFF3B3B3B)
private val DarkSearch = Color(0xFF454545) // not in provided swatch — unchanged
private val DarkDivider = Color(0xFF72868F) // not in provided swatch — unchanged

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