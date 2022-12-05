package ru.rikmasters.gilty.profile.presentation.ui.mymeetings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.MeetingBottomSheetTopBarCompose
import ru.rikmasters.gilty.shared.common.ProfileMeetingBottomSheetCallback
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.DIVIDE
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.FREE
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.NO_MATTER
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.ORGANIZER_PAY
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MyMeetingPreview() {
    GiltyTheme {
        val meet = DemoFullMeetingModel
        MyMeeting(
            Modifier,
            MyMeetingState(
                meet, DemoMemberModelList,
                18, meet.duration,
                (false), (false)
            )
        )
    }
}


interface MyMeetingCallback: ProfileMeetingBottomSheetCallback {
    
    fun onCloseClick()
    fun onAllWatchClick()
    fun menuCollapse(it: Boolean)
    fun menuItemClick(point: Int)
    fun closeAlert()
}

data class MyMeetingState(
    val meetingModel: FullMeetingModel,
    val memberList: List<MemberModel>,
    val distance: Int,
    val eventDuration: String,
    val menuState: Boolean,
    val alert: Boolean,
)

@Composable
fun MyMeeting(
    modifier: Modifier = Modifier,
    state: MyMeetingState,
    callback: MyMeetingCallback? = null
) {
    GAlert(
        state.alert, { callback?.closeAlert() },
        "Отлично, ваша жалоба отправлена!",
        label = "Модераторы скоро рассмотрят\nвашу жалобу",
        success = Pair("Закрыть") { callback?.closeAlert() }
    )
    LazyColumn(
        modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {
        item {
            MeetingBottomSheetTopBarCompose(
                Modifier, state.meetingModel,
                state.eventDuration, state.menuState,
                { callback?.menuCollapse(it) },
                { callback?.menuItemClick(it) }
            )
        }
        item {
            Card(
                Modifier
                    .padding(top = 13.dp)
                    .fillMaxWidth(),
                MaterialTheme.shapes.large,
                CardDefaults.cardColors(
                    colorScheme.primaryContainer
                )
            ) {
                Text(
                    state.meetingModel.title,
                    Modifier.padding(14.dp),
                    color = colorScheme.tertiary,
                    style = typography.bodyMedium
                )
            }
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
                    when(state.meetingModel.type) {
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
                val condition = state.meetingModel.condition
                Row(
                    Modifier.fillMaxWidth(),
                    SpaceBetween, CenterVertically
                ) {
                    Row(Modifier.padding(16.dp), Start, CenterVertically) {
                        if(condition == MEMBER_PAY)
                            Image(
                                painterResource(R.drawable.ic_money),
                                (null),
                                Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp)
                            )
                        Text(
                            when(state.meetingModel.condition) {
                                FREE -> stringResource(R.string.condition_free)
                                DIVIDE -> stringResource(R.string.condition_divide)
                                MEMBER_PAY -> stringResource(R.string.condition_member_pay)
                                NO_MATTER -> stringResource(R.string.condition_no_matter)
                                ORGANIZER_PAY -> stringResource(R.string.condition_organizer_pay)
                            }, Modifier,
                            colorScheme.tertiary, style = typography.bodyMedium
                        )
                    }
                    if(condition == MEMBER_PAY) Text(
                        "${state.meetingModel.price ?: "0"} ₽",
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
                    "${state.memberList.size}/${state.meetingModel.memberCount}",
                    Modifier.padding(start = 8.dp),
                    colorScheme.primary,
                    style = typography.labelLarge
                )
                Image(
                    painterResource(
                        if(state.meetingModel.isPrivate) R.drawable.ic_lock_close
                        else R.drawable.ic_lock_open
                    ), null,
                    Modifier.padding(start = 8.dp)
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
                state.memberList.forEachIndexed { index, member ->
                    if(index < 3) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { callback?.onMemberClick() },
                            SpaceBetween,
                            CenterVertically
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
                        if(state.memberList.size <= 3 && index + 1 < state.memberList.size) {
                            Divider(Modifier.padding(start = 60.dp))
                        } else if(index + 1 < 3) Divider(Modifier.padding(start = 60.dp))
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
                    .clickable { callback?.onAllWatchClick() },
                colorScheme.primary,
                style = typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            GradientButton(
                Modifier.padding(top = 20.dp, bottom = 12.dp),
                stringResource(R.string.meeting_shared_button),
                icon = R.drawable.ic_shared
            ) { callback?.onConfirm() }
        }
        item {
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Text(
                    stringResource(R.string.meeting_close_button),
                    Modifier
                        .padding(bottom = 28.dp)
                        .clip(CircleShape)
                        .clickable { callback?.onCloseClick() },
                    colorScheme.tertiary,
                    style = typography.bodyLarge,
                )
            }
        }
    }
}