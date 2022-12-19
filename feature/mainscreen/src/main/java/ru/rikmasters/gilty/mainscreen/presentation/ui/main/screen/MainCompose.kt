package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.grid.MeetingGridContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe.MeetingsListContent
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.*
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun MainContentPreview() {
    GiltyTheme {
        MainContent(
            MainContentState(
                (true), Pair(true, false),
                DemoMeetingList, listOf(),
                listOf(
                    INACTIVE, ACTIVE,
                    INACTIVE, NEW, INACTIVE
                ), (false)
            )
        )
    }
}

interface MainContentCallback {
    
    fun onTodayChange() {}
    fun onTimeFilterClick() {}
    fun onStyleChange() {}
    fun onRespond(meet: FullMeetingModel) {}
    fun onMeetClick(meet: FullMeetingModel) {}
    fun onNavBarSelect(point: Int) {}
    fun openFiltersBottomSheet() {}
    fun interesting(state: SwipeableCardState) {}
    fun notInteresting(state: SwipeableCardState) {}
    fun closeAlert()
}

data class MainContentState(
    val grid: Boolean,
    val switcher: Pair<Boolean, Boolean>,
    val meetings: List<FullMeetingModel>,
    val cardStates: List<Pair<FullMeetingModel, SwipeableCardState>>,
    val navBarStates: List<NavIconState>,
    val alert: Boolean,
)

@Composable
fun MainContent(
    state: MainContentState,
    modifier: Modifier = Modifier,
    callback: MainContentCallback? = null,
) {
    Column(
        Modifier
            .background(colorScheme.background)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 10.dp),
            Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                GiltyString(
                    Modifier.padding(end = 12.dp),
                    stringResource(R.string.meeting_profile_bottom_today_label),
                    state.switcher.first
                ) { callback?.onTodayChange() }
                GiltyString(
                    Modifier,
                    stringResource(R.string.meeting_profile_bottom_latest_label),
                    state.switcher.second
                ) { callback?.onTodayChange() }
            }
            IconButton({ callback?.onTimeFilterClick() }) {
                Icon(
                    if(state.switcher.first)
                        painterResource(R.drawable.ic_clock)
                    else painterResource(R.drawable.ic_calendar),
                    null,
                    Modifier.size(30.dp),
                    colorScheme.tertiary
                )
            }
        }
        if(state.grid)
            MeetingGridContent(
                Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.90f),
                state.meetings
            ) { callback?.onRespond(it) }
        else {
            MeetingsListContent(
                state.cardStates,
                Modifier.fillMaxHeight(0.84f),
                { callback?.notInteresting(it) },
                { meet, it ->
                    callback?.onRespond(meet)
                    callback?.interesting(it)
                }
            ) { callback?.onMeetClick(it) }
        }
    }
    Box(Modifier.fillMaxSize()) {
        NavBar(
            state.navBarStates,
            Modifier.align(Alignment.BottomCenter)
        ) { callback?.onNavBarSelect(it) }
        DividerBold(
            Modifier
                .padding(horizontal = 180.dp)
                .padding(bottom = 92.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter)
                .clickable { callback?.openFiltersBottomSheet() }
        )
        SquareCheckBox(
            !state.grid,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 92.dp)
        ) { callback?.onStyleChange() }
    }
    GAlert(
        state.alert, { callback?.closeAlert() },
        "Отлично, ваша жалоба отправлена!",
        label = "Модераторы скоро рассмотрят\nвашу жалобу",
        success = Pair("Закрыть") { callback?.closeAlert() }
    )
}

@Preview
@Composable
fun MeetingPreview() {
    GiltyTheme {
        MeetingSwipe(
            (false), (null), (null),
            DemoFullMeetingModel,
            (true), ("")
        )
    }
}

@Composable
fun MeetingSwipe(
    menuState: Boolean,
    menuCollapse: ((Boolean) -> Unit)? = null,
    menuItemClick: ((Int) -> Unit)? = null,
    meet: FullMeetingModel,
    hiddenPhoto: Boolean,
    commentText: String,
    callback: MeetingDetailsBottomCallback? = null
) {
    Column(
        Modifier
            .background(colorScheme.background)
            .padding(16.dp)
            .padding(bottom = 40.dp)
    ) {
        MeetingBottomSheetTopBarCompose(
            Modifier, MeetingBottomSheetTopBarState(
                meet, meet.duration, menuState
            ), { menuCollapse?.let { c -> c(it) } },
            { menuItemClick?.let { c -> c(it) } }
        )
        MeetingDetailsBottomCompose(
            Modifier.padding(top = 30.dp),
            MeetingDetailsBottomComposeState(hiddenPhoto, commentText),
            callback
        )
    }
}

@Composable
fun MeetingClick(
    menuState: Boolean,
    menuCollapse: ((Boolean) -> Unit)? = null,
    menuItemClick: ((Int) -> Unit)? = null,
    meet: FullMeetingModel,
    membersList: List<MemberModel>,
    onRespond: (() -> Unit)? = null,
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
                style = MaterialTheme.typography.labelLarge
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
                    style = MaterialTheme.typography.bodyMedium
                )
                androidx.compose.material3.Divider(Modifier.padding(start = 16.dp))
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
                            }, Modifier,
                            colorScheme.tertiary, style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if(condition == ConditionType.MEMBER_PAY) Text(
                        "${meet.price ?: "0"} ₽",
                        Modifier.padding(end = 16.dp), colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
        item {
            Row(Modifier.padding(top = 28.dp)) {
                Text(
                    stringResource(R.string.meeting_members), Modifier,
                    colorScheme.tertiary,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    "${membersList.size}/${meet.memberCount}",
                    Modifier.padding(start = 8.dp),
                    colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
                Image(
                    painterResource(
                        if(meet.isPrivate) R.drawable.ic_lock_close
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
                                Filled.KeyboardArrowRight,
                                null,
                                Modifier.padding(end = 16.dp),
                                colorScheme.tertiary
                            )
                        }
                        if(membersList.size <= 3 && index + 1 < membersList.size) {
                            androidx.compose.material3.Divider(Modifier.padding(start = 60.dp))
                        } else if(index + 1 < 3) androidx.compose.material3.Divider(
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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            GradientButton(
                Modifier.padding(top = 20.dp, bottom = 12.dp),
                stringResource(R.string.meeting_respond)
            ) { onRespond?.let { it() } }
        }
    }
}