package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.FillHeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.string.meeting_anon_type
import ru.rikmasters.gilty.shared.R.string.meeting_private_and_anon_type
import ru.rikmasters.gilty.shared.R.string.meeting_private_type
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.getDemoMeetingModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
private fun MeetingBSTopBarPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBSTopBarCompose(
                Modifier.padding(16.dp),
                MeetingBSTopBarState(
                    getDemoMeetingModel(
                        type = ANONYMOUS,
                        isPrivate = true
                    ), (false), respondsCount = 3,
                    lastRespond = DemoSendRespondsModel,
                    responds = true
                )
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBSTopBarOnlinePreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBSTopBarCompose(
                Modifier.padding(16.dp),
                MeetingBSTopBarState(
                    getDemoMeetingModel(isOnline = true),
                    (false)
                )
            )
        }
    }
}

data class MeetingBSTopBarState(
    val meet: MeetingModel,
    val menuState: Boolean = false,
    val responds: Boolean = false,
    val respondsCount: Int? = null,
    val lastRespond: RespondModel? = null
)

@Composable
fun MeetingBSTopBarCompose(
    modifier: Modifier = Modifier,
    state: MeetingBSTopBarState,
    menuCollapse: ((Boolean) -> Unit)? = null,
    menuItemClick: ((Int) -> Unit)? = null,
    onRespondsClick: (() -> Unit)? = null,
    onAvatarClick: (() -> Unit)? = null,
) {
    val org = state.meet.organizer
    val anonymous = state.meet.type == ANONYMOUS
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), SpaceBetween) {
            Text(
                state.meet.title,
                Modifier, colorScheme.tertiary,
                style = typography.titleLarge
            )
            GDropMenu(
                state.menuState,
                { menuCollapse?.let { it(false) } },
                menuItem = listOf(Pair(stringResource(R.string.meeting_complain))
                { menuItemClick?.let { it(0) } })
            )
            GKebabButton { menuCollapse?.let { it(true) } }
        }
        if(state.responds) Responds(
            stringResource(R.string.profile_responds_label),
            state.respondsCount,
            state.lastRespond?.sender?.avatar
        ) { onRespondsClick?.let { it() } }
        Row(
            Modifier
                .padding(top = 12.dp)
                .height(IntrinsicSize.Max)
        ) {
            Avatar(org.avatar, Modifier.weight(1f))
            { onAvatarClick?.let { it() } }
            Spacer(Modifier.width(18.dp))
            Meet(state.meet, Modifier.weight(1f))
        }
        Row(verticalAlignment = CenterVertically) {
            BrieflyRow((null), ("${org.username}, ${org.age}"), org.emoji)
            Text(
                when {
                    anonymous && state.meet.isPrivate ->
                        stringResource(meeting_private_and_anon_type)
                    
                    anonymous ->
                        stringResource(meeting_anon_type)
                    
                    state.meet.isPrivate ->
                        stringResource(meeting_private_type)
                    
                    else -> ""
                }, Modifier, colorScheme.onTertiary,
                style = typography.labelSmall
            )
        }
    }
}

@Composable
private fun Meet(
    meet: MeetingModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier, ThemeExtra.shapes.cardShape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier, Top, End) {
            CategoryItem(
                meet.category, (true),
                Modifier.offset(12.dp, -(18).dp)
            )
            Box(Modifier.fillMaxWidth()) {
                MeetDetails(
                    meet, Modifier
                        .offset(y = -(12).dp)
                        .align(BottomEnd)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun MeetDetails(
    meet: MeetingModel,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            stringResource(R.string.meeting_profile_bottom_today_label),
            Modifier.padding(bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold,
            maxLines = 1,
            overflow = Ellipsis
        )
        Row(Modifier, SpaceBetween) {
            DateTimeCard(
                DemoMeetingModel.dateTime,
                if(meet.isOnline) listOf(
                    colorScheme.secondary,
                    colorScheme.secondary
                ) else listOf(
                    meet.category.color,
                    meet.category.color
                ), (true)
            )
            Box(
                Modifier
                    .padding(start = 4.dp)
                    .clip(shapes.extraSmall)
                    .background(colorScheme.outlineVariant)
            ) {
                Text(
                    meet.duration,
                    Modifier.padding(12.dp, 6.dp),
                    White, fontWeight = SemiBold,
                    style = typography.labelSmall,
                    maxLines = 1,
                    overflow = Ellipsis
                )
            }
        }
    }
}

@Composable
private fun Avatar(
    avatar: AvatarModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .background(
                colorScheme.primaryContainer,
                shapes.large
            )
    ) {
        AsyncImage(
            avatar.id, (null),
            Modifier
                .fillMaxWidth()
                .clip(shapes.large)
                .clickable { onClick() },
            contentScale = FillHeight
        )
    }
}