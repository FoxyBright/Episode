package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.MeetingBottomSheetTopBarCompose
import ru.rikmasters.gilty.shared.common.MeetingBottomSheetTopBarState
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetBottomSheet(
    // TODO - пока дублируется позже вынести в shared
    menuState: Boolean,
    menuCollapse: ((Boolean) -> Unit)? = null,
    menuItemClick: ((Int) -> Unit)? = null,
    meet: FullMeetingModel,
    membersList: List<MemberModel>,
    onExit: (() -> Unit)? = null,
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorScheme.background)
    ) {
        item {
            MeetingBottomSheetTopBarCompose(
                Modifier, MeetingBottomSheetTopBarState(
                    meet, meet.duration, menuState
                ), { menuCollapse?.let { c -> c(it) } },
                { menuItemClick?.let { c -> c(it) } }
            )
        }
        item {
            Text(
                stringResource(R.string.meeting_terms),
                Modifier.padding(top = 28.dp),
                color = colorScheme.tertiary,
                style = typography.labelLarge
            )
        }
        item {
            Card(
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                MaterialTheme.shapes.extraSmall,
                CardDefaults.cardColors(
                    colorScheme.primaryContainer
                )
            ) {
                Text(
                    when(meet.type) {
                        MeetType.GROUP -> stringResource(R.string.meeting_group_type)
                        MeetType.ANONYMOUS -> stringResource(R.string.meeting_anon_type)
                        MeetType.PERSONAL -> stringResource(R.string.meeting_personal_type)
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium
                )
                Divider(Modifier.padding(start = 16.dp))
                val condition = meet.condition
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween, Alignment.CenterVertically
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        Arrangement.Start,
                        Alignment.CenterVertically
                    ) {
                        if(condition == ConditionType.MEMBER_PAY)
                            Image(
                                painterResource(R.drawable.ic_money),
                                (null),
                                Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp)
                            )
                        Text(
                            when(meet.condition) {
                                ConditionType.FREE -> stringResource(R.string.condition_free)
                                ConditionType.DIVIDE -> stringResource(R.string.condition_divide)
                                ConditionType.MEMBER_PAY -> stringResource(R.string.condition_member_pay)
                                ConditionType.NO_MATTER -> stringResource(R.string.condition_no_matter)
                                ConditionType.ORGANIZER_PAY -> stringResource(R.string.condition_organizer_pay)
                            },
                            Modifier,
                            colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                    }
                    if(condition == ConditionType.MEMBER_PAY) Text(
                        "${meet.price ?: "0"} ₽",
                        Modifier.padding(end = 16.dp), colorScheme.primary,
                        style = typography.headlineLarge
                    )
                }
            }
        }
        item {
            Row(Modifier.padding(top = 28.dp)) {
                Text(
                    stringResource(R.string.meeting_members), Modifier,
                    colorScheme.tertiary,
                    style = typography.labelLarge
                )
                Text(
                    "${membersList.size}/${meet.memberCount}",
                    Modifier.padding(start = 8.dp),
                    colorScheme.primary,
                    style = typography.labelLarge
                )
                Image(
                    painterResource(
                        if(meet.isPrivate) R.drawable.ic_lock_close
                        else R.drawable.ic_lock_open
                    ), (null), Modifier.padding(start = 8.dp),
                    colorFilter = tint(colorScheme.tertiary)
                )
            }
        }
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(colorScheme.primaryContainer)
            ) {
                membersList.forEachIndexed { index, member ->
                    if(index < 3) {
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            BrieflyRow(
                                member.avatar,
                                "${member.username}, ${member.age}",
                                modifier = Modifier.padding(12.dp, 8.dp)
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                null,
                                Modifier.padding(end = 16.dp),
                                colorScheme.tertiary
                            )
                        }
                        if(membersList.size <= 3 && index + 1 < membersList.size) {
                            Divider(Modifier.padding(start = 60.dp))
                        } else if(index + 1 < 3) Divider(
                            Modifier.padding(
                                start = 60.dp
                            )
                        )
                    }
                }
            }
        }
        item {
            Text(
                stringResource(R.string.meeting_watch_all_members_in_meet),
                Modifier
                    .padding(top = 12.dp)
                    .clip(CircleShape)
                    .clickable { /*TODO клик на смотреть всех участников*/ },
                colorScheme.primary,
                style = typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            Row(
                Modifier.padding(
                    top = 28.dp,
                    bottom = 18.dp
                )
            ) {
                Text(
                    stringResource(R.string.meeting_distance_from_user),
                    Modifier.padding(end = 8.dp),
                    colorScheme.tertiary,
                    style = typography.labelLarge
                )
                Text(     // TODO здесь нужно вычислять расстояние от пользователя
                    "18 км", Modifier,
                    colorScheme.primary,
                    style = typography.labelLarge
                )
            }
        }
        item {
            Card(
                { /*TODO Тут переход на карты*/ },
                Modifier.fillMaxWidth(), (true), MaterialTheme.shapes.medium,
                CardDefaults.cardColors(colorScheme.primaryContainer)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        meet.address,
                        Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp),
                        colorScheme.onTertiary,
                        style = typography.headlineSmall
                    )
                    Text(
                        meet.place,
                        Modifier
                            .padding(top = 2.dp, bottom = 10.dp)
                            .padding(horizontal = 16.dp),
                        colorScheme.tertiary,
                        style = typography.bodyMedium
                    )
                }
            }
        }
        item {
            Text(
                stringResource(R.string.exit_from_meet),
                Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 32.dp)
                    .clickable(
                        MutableInteractionSource(), (null)
                    ) { onExit?.let { it() } },
                colorScheme.primary, textAlign = TextAlign.Center,
                style = typography.bodyLarge
            )
        }
    }
}