package ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.IntrinsicSize.Max
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bottomsheet.presentation.ui.meet.MeetingBsCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.profileBadges.ProfileBadge
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.LastRespond.Companion.DemoLastRespond
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.COMPLETED
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun MeetingBsTopBarPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsTopBarCompose(
                Modifier.padding(16.dp),
                MeetingBsTopBarState(
                    meet = DemoFullMeetingModel
                        .copy(
                            type = ANONYMOUS,
                            isPrivate = true,
                            description = "hello"
                        ),
                    lastRespond = DemoLastRespond,
                )
            )
        }
    }
}

@Preview
@Composable
private fun MeetingBsTopBarOnlinePreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingBsTopBarCompose(
                Modifier.padding(16.dp),
                MeetingBsTopBarState(
                    meet = DemoFullMeetingModel
                        .copy(isOnline = true),
                    backButton = true,
                    lastRespond = DemoLastRespond,
                )
            )
        }
    }
}

data class MeetingBsTopBarState(
    val meet: FullMeetingModel,
    val lastRespond: LastRespond?,
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
            && (last?.count ?: 0) > 0
        ) last?.let {
            Responds(
                it,
                Modifier.padding(bottom = 12.dp)
            ) { callback?.onRespondsClick(state.meet) }
        }
        Row(Modifier.height(Max)) {
            Avatar(
                avatar = org.avatar?.thumbnail?.url,
                modifier = Modifier.weight(1f)
            ) {
                org.id?.let {
                    callback?.onAvatarClick(
                        organizerId = it,
                        meetId = state.meet.id
                    )
                }
            }
            Spacer(Modifier.width(18.dp))
            Meet(
                meet = state.meet,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            Modifier.padding(
                top = 10.dp,
                bottom = 12.dp
            ), Start, CenterVertically
        ) {
            BrieflyRow(
                text = "${org.username}${
                    if(org.age in 18..99) {
                        ", ${org.age}"
                    } else ""
                }",
                emoji = org.emoji,
                
                group = org.group ?: UserGroupTypeModel.DEFAULT
            )
            
            ProfileBadge(
                group = state.meet.organizer.group
                    ?: UserGroupTypeModel.DEFAULT,
                modifier = Modifier
                    .padding(start = 6.dp),
                labelSize = 8,
                textPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 3.dp
                )
            )
            
            Text(
                modifier = Modifier
                    .padding(start = 6.dp),
                text = state.meet.display(),
                color = colorScheme.onTertiary,
                style = typography.labelSmall
            )
        }
        if(state.meet.description.isNotBlank())
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorScheme
                            .primaryContainer,
                        shape = shapes.large
                    )
            ) {
                Text(
                    text = state.meet
                        .description,
                    modifier = Modifier
                        .padding(14.dp),
                    color = colorScheme
                        .tertiary,
                    style = typography
                        .bodyMedium
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
        modifier = modifier
            .width(156.dp)
            .height(150.dp),
        shape = shapes.large,
        colors = cardColors(
            colorScheme.primaryContainer
        )
    ) {
        Column(Modifier, Top, End) {
            meet.category.let {
                CategoryItem(
                    name = it.name,
                    icon = it.emoji,
                    color = when {
                        meet.status == COMPLETED ->
                            colorScheme.onTertiary
                        meet.isOnline ->
                            colorScheme.secondary
                        else -> it.color
                    },
                    state = true,
                    size = 92,
                    modifier = Modifier
                        .offset(12.dp, -(18).dp)
                )
            }
            MeetDetails(
                meet = meet,
                modifier = Modifier
                    .offset(y = -(10).dp)
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
            text = if(todayControl(meet.datetime))
                stringResource(R.string.meeting_profile_bottom_today_label)
            else meet.datetime.dateCalendar(),
            modifier = Modifier.padding(bottom = 8.dp),
            style = typography.bodyMedium.copy(
                color = colorScheme.tertiary,
                fontSize = 16.dp.toSp(),
                fontWeight = Bold
            ),
        )
        Row(Modifier.fillMaxWidth(), SpaceBetween) {
            SubLabel(
                text = meet.datetime.format("HH:mm"),
                color = when {
                    meet.status == COMPLETED
                            || meet.status == MeetStatusType.CANCELED ->
                        colorScheme.onTertiary
                    meet.isOnline ->
                        colorScheme.secondary
                    else -> meet.category.color
                },
                shape = 10.dp
            )
            SubLabel(
                text = meet.duration,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                color = if(meet.status == COMPLETED
                    || meet.status == MeetStatusType.CANCELED
                ) colorScheme.onTertiary
                else colorScheme.outlineVariant,
                shape = 6.dp
            )
        }
    }
}

@Composable
private fun SubLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    shape: Dp,
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(shape)
            ),
        contentAlignment = Center
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(12.dp, 3.dp),
            style = typography
                .labelSmall.copy(
                    fontSize = 14
                        .dp.toSp(),
                    color = White,
                    fontWeight = Bold,
                )
        )
    }
}

@Composable
private fun Avatar(
    avatar: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(156.dp, 150.dp)
            .background(
                color = colorScheme
                    .primaryContainer,
                shape = shapes.large
            )
    ) {
        GCachedImage(
            url = avatar,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.large)
                .clickable { onClick() },
            contentScale = Crop
        )
    }
}