package app.voqal.com.feature.room.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import app.voqal.com.feature.room.domain.NewsRoomUi
import app.voqal.com.feature.room.domain.ParticipantUi
import org.jetbrains.compose.resources.vectorResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_chat
import voqal.shared.generated.resources.ic_person

/**
 * The three room accent colors from the design system.
 * Cards cycle through these so a list of rooms reads as visually varied,
 * matching the Figma list view (blue -> warm -> slate, repeating).
 */
enum class RoomColorVariant {
    BLUE,
    WARM,
    SLATE;

    companion object {
        /** Picks a variant by position, e.g. itemsIndexed { index, _ -> } -> forIndex(index) */
        fun forIndex(index: Int): RoomColorVariant = entries[index % entries.size]
    }
}

@Composable
fun NewsRoomCard(
    room: NewsRoomUi,
    modifier: Modifier = Modifier,
    colorVariant: RoomColorVariant = RoomColorVariant.BLUE,
    onClick: (String) -> Unit = {},
    onMoreClick: (String) -> Unit = {},
    onParticipantClick: (String) -> Unit = {},
) {
    val containerColor = when (colorVariant) {
        RoomColorVariant.BLUE -> VoqalTheme.extendedColors.roomBlue
        RoomColorVariant.WARM -> VoqalTheme.extendedColors.roomWarm
        RoomColorVariant.SLATE -> VoqalTheme.extendedColors.roomSlate
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(room.id)
            },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            NewsRoomHeader(
                category = room.category,
                title = room.title,
                onMoreClick = {
                    onMoreClick(room.id)
                }
            )

            Spacer(Modifier.height(16.dp))

            DashedDivider()

            Spacer(Modifier.height(20.dp))

            Row {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    MemberAvatarGroup(
                        participants = room.participants
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        StatChip(
                            icon = vectorResource(Res.drawable.ic_person),
                            text = room.listenerCount.toString()
                        )

                        StatChip(
                            icon = vectorResource(Res.drawable.ic_chat),
                            text = room.commentCount.toString()
                        )
                    }
                }

                Spacer(Modifier.width(24.dp))

                ParticipantList(
                    participants = room.participants,
                    onClick = onParticipantClick
                )
            }
        }
    }
}



@PreviewLightDark
@Composable
private fun NewsRoomCardPreview() {
    VoqalTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            Column {
                NewsRoomCard(
                    room = NewsRoomUi(
                        id = "room_1",
                        category = "NEWS NEWS \uD83C\uDF89",
                        title = "3 Minute News",
                        participants = listOf(
                            ParticipantUi(id = "1", name = "Lena Marsh"),
                            ParticipantUi(id = "2", name = "Minerva Spencer"),
                            ParticipantUi(id = "3", name = "John Carter"),
                        ),
                        listenerCount = 155,
                        commentCount = 3
                    ),
                    colorVariant = RoomColorVariant.BLUE
                )

                Spacer(Modifier.height(12.dp))

                NewsRoomCard(
                    room = NewsRoomUi(
                        id = "room_2",
                        category = "BUSINESS ENTREPRENEURSHIP",
                        title = "Live Mastermind",
                        participants = listOf(
                            ParticipantUi(id = "4", name = "Jon Daniels"),
                            ParticipantUi(id = "5", name = "Della Guerrero"),
                            ParticipantUi(id = "6", name = "Blake Vega"),
                        ),
                        listenerCount = 49,
                        commentCount = 12
                    ),
                    colorVariant = RoomColorVariant.WARM
                )

                Spacer(Modifier.height(12.dp))

                NewsRoomCard(
                    room = NewsRoomUi(
                        id = "room_3",
                        category = "TED OFFICIAL",
                        title = "Baajaveri Hedhunu",
                        participants = listOf(
                            ParticipantUi(id = "7", name = "Christina Norton"),
                            ParticipantUi(id = "8", name = "Justin Hart"),
                        ),
                        listenerCount = 15,
                        commentCount = 2
                    ),
                    colorVariant = RoomColorVariant.SLATE
                )
            }
        }
    }
}