package app.voqal.com.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.voqal.com.core.designsystem.theme.VoqalTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import voqal.shared.generated.resources.Res
import voqal.shared.generated.resources.ic_active_tab
import voqal.shared.generated.resources.ic_event
import voqal.shared.generated.resources.ic_home
import voqal.shared.generated.resources.ic_notification

enum class VoqalBottomNavTab {
    Home,
    Events,
    Notifications
}

@Immutable
data class VoqalBottomNavItem(
    val tab: VoqalBottomNavTab,
    val icon: DrawableResource,
    val contentDescription: String
)

private val DefaultVoqalBottomNavItems = listOf(
    VoqalBottomNavItem(
        tab = VoqalBottomNavTab.Home,
        icon = Res.drawable.ic_home,
        contentDescription = "Home"
    ),
    VoqalBottomNavItem(
        tab = VoqalBottomNavTab.Events,
        icon = Res.drawable.ic_event,
        contentDescription = "Events"
    ),
    VoqalBottomNavItem(
        tab = VoqalBottomNavTab.Notifications,
        icon = Res.drawable.ic_notification,
        contentDescription = "Notifications"
    )
)

@Composable
fun VoqalBottomNavigationBar(
    selectedTab: VoqalBottomNavTab,
    onTabClick: (VoqalBottomNavTab) -> Unit,
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
    items: List<VoqalBottomNavItem> = DefaultVoqalBottomNavItems
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp))
            .background(Color.White)
            .padding(start = 28.dp, end = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                VoqalBottomNavIconButton(
                    item = item,
                    selected = item.tab == selectedTab,
                    onClick = { onTabClick(item.tab) }
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        CreateRoomButton(
            onClick = onCreateRoomClick,
            width = 58.dp,
            height = 52.dp
        )
    }
}

@Composable
private fun VoqalBottomNavIconButton(
    item: VoqalBottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 86.dp, height = 68.dp)
            .clip(RoundedCornerShape(34.dp))
            .clickable(
                role = Role.Tab,
                onClickLabel = item.contentDescription,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Image(
                painter = painterResource(Res.drawable.ic_active_tab),
                contentDescription = null,
                modifier = Modifier.size(width = 98.dp, height = 78.dp)
            )
        }

        Image(
            painter = painterResource(item.icon),
            contentDescription = item.contentDescription,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 140)
@Composable
private fun VoqalBottomNavigationBarHomePreview() {
    VoqalTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(top = 24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            VoqalBottomNavigationBar(
                selectedTab = VoqalBottomNavTab.Home,
                onTabClick = {},
                onCreateRoomClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 140)
@Composable
private fun VoqalBottomNavigationBarEventsPreview() {
    VoqalTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(top = 24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            VoqalBottomNavigationBar(
                selectedTab = VoqalBottomNavTab.Events,
                onTabClick = {},
                onCreateRoomClick = {}
            )
        }
    }
}
