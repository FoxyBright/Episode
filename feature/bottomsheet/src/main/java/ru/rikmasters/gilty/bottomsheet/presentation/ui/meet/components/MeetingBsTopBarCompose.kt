package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components

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
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.MeetingBsCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.common.GCashedImage
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.dateCalendar
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.COMPLETED
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.shared.DateTimeCard
import ru.rikmasters.gilty.shared.theme.Gradients.gray
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBsTopBarPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsTopBarCompose(
                Modifier.padding(16.dp),
                MeetingBsTopBarState(
                    DemoFullMeetingModel.copy(
                        type = ANONYMOUS,
                        isPrivate = true,
                        description = "hello"
                    ),
                    (false),
                    lastRespond = 4 to "",
                )
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsTopBarOnlinePreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            MeetingBsTopBarCompose(
                Modifier.padding(16.dp),
                MeetingBsTopBarState(
                    DemoFullMeetingModel.copy(isOnline = true),
                    (false), backButton = true,
                    lastRespond = 4 to ""
                )
            )
        }
    }
}

data class MeetingBsTopBarState(
    val meet: FullMeetingModel,
    val menuState: Boolean = false,
    val lastRespond: Pair<Int, String?>?,
    val description: Boolean = false,
    val backButton: Boolean = false,
)

@Composable
fun MeetingBsTopBarCompose(
    modifier: Modifier = Modifier,
    state: MeetingBsTopBarState,
    callback: MeetingBsCallback? = null,
) {
    val org = state.meet.organizer
    Column(modifier) {
        val last = state.lastRespond
        if(
            state.lastRespond != null &&
            state.meet.memberState == IS_ORGANIZER
            && (last?.first ?: 0) > 0
        ) Responds(
            last?.second, last?.first,
            Modifier.padding(bottom = 12.dp)
        ) { callback?.onRespondsClick(state.meet) }
        Row(Modifier.height(IntrinsicSize.Max)) {
            Avatar(org.avatar?.thumbnail?.url, Modifier.weight(1f))
            { org.id?.let { callback?.onAvatarClick(it, state.meet.id) } }
            Spacer(Modifier.width(18.dp))
            Meet(state.meet, Modifier.weight(1f))
        }
        Row(
            Modifier.padding(top = 10.dp, bottom = 12.dp),
            Start, CenterVertically
        ) {
            BrieflyRow(
                "${org.username}${
                    if (org.age in 18..99) {
                        ", ${org.age}"
                    } else ""
                }",
                Modifier, (null), org.emoji
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
private fun FullMeetingModel.display(): String {
    val anonymous = this.type == ANONYMOUS
    return stringResource(
        when {
            isPrivate && anonymous -> R.string.meeting_private_and_anon_type
            anonymous -> R.string.meeting_anon_type
            isPrivate -> R.string.meeting_private_type
            else -> R.string.empty_String
        }
    )
}

@Composable
private fun Meet(
    meet: FullMeetingModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier, shapes.large,
        cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier, Top, End) {
            val category = meet.category
            CategoryItem(
                category.name, category.emoji,
                if(meet.status == COMPLETED) colorScheme.onTertiary
                else if(meet.isOnline) colorScheme.secondary
                else category.color, (true),
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
    meet: FullMeetingModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            if(todayControl(meet.datetime))
                stringResource(R.string.meeting_profile_bottom_today_label)
            else meet.datetime.dateCalendar(),
            Modifier.padding(bottom = 8.dp),
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = Bold
        )
        Row(Modifier, SpaceBetween) {
            DateTimeCard(
                meet.datetime,
                if(meet.status == COMPLETED) gray()
                else if(meet.isOnline) listOf(
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
    avatar: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .background(
                colorScheme.primaryContainer,
                shapes.large
            )
    ) {
        GCashedImage(
            avatar, Modifier
                .fillMaxWidth()
                .clip(shapes.large)
                .clickable { onClick() },
            contentScale = Crop
        )
    }
}