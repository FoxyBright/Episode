package ru.rikmasters.gilty.shared.common.meetBS

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.FillHeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.dateCalendar
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.getDemoMeetingModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

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
    val lastRespond: RespondModel? = null,
    val description: Boolean = false
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
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                state.meet.title,
                Modifier, colorScheme.tertiary,
                style = typography.labelLarge
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
            state.lastRespond?.sender?.avatar,
            Modifier.padding(bottom = 12.dp)
        ) { onRespondsClick?.let { it() } }
        Row(Modifier.height(IntrinsicSize.Max)) {
            Avatar(org.avatar, Modifier.weight(1f))
            { onAvatarClick?.let { it() } }
            Spacer(Modifier.width(18.dp))
            Meet(state.meet, Modifier.weight(1f))
        }
        Row(
            Modifier.padding(top = 10.dp, bottom = 12.dp),
            Start, CenterVertically
        ) {
            BrieflyRow(
                (null), ("${org.username}, ${org.age}"),
                org.emoji
            )
            Text(
                state.meet.display(),
                Modifier, colorScheme.onTertiary,
                style = typography.labelSmall
            )
        }
        if(state.description && state.meet
                .description.isNotBlank()
        ) Box(
            Modifier
                .fillMaxWidth()
                .background(
                    colorScheme.primaryContainer,
                    shapes.large
                )
        ) {
            Text(
                state.meet.description,
                Modifier.padding(14.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MeetingModel.display(): String {
    val anonymous = this.type == ANONYMOUS
    val private = isPrivate
    return stringResource(
        when {
            private && anonymous -> R.string.meeting_private_and_anon_type
            anonymous -> R.string.meeting_anon_type
            private -> R.string.meeting_private_type
            else -> R.string.empty_String
        }
    )
}

@Composable
private fun Meet(
    meet: MeetingModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier, shapes.large,
        cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier, Top, End) {
            val category = meet.category
            CategoryItem(
                category.display, category.emoji,
                category.color, (true),
                Modifier.offset(12.dp, -(18).dp)
            )
            MeetDetails(
                meet, Modifier
                    .offset(y = -(12).dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
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
            if(todayControl(meet.dateTime))
                stringResource(R.string.meeting_profile_bottom_today_label)
            else meet.dateTime.dateCalendar(),
            Modifier.padding(bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold
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
                ), (true), Modifier.weight(1f)
            )
            Box(
                Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.outlineVariant),
                Center
            ) {
                Text(
                    meet.duration,
                    Modifier.padding(6.dp),
                    White, fontWeight = SemiBold,
                    style = typography.labelSmall
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