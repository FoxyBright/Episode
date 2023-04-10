package ru.rikmasters.gilty.mainscreen.presentation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.MeetCard
import ru.rikmasters.gilty.shared.common.MeetCardType.EMPTY
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW_INACTIVE
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun MainContentPreview() {
    GiltyTheme {
        MainContent(
            MainContentState(
                (false), (false), (false), (false),
                DemoMeetingList, listOf(
                    INACTIVE, ACTIVE,
                    INACTIVE, NEW_INACTIVE, INACTIVE
                ), (false), (false)
            )
        )
    }
}

@Preview
@Composable
fun GridMainContentPreview() {
    GiltyTheme {
        MainContent(
            MainContentState(
                (true), (true), (false), (false),
                DemoMeetingList, listOf(
                    INACTIVE, ACTIVE,
                    INACTIVE, NEW_INACTIVE, INACTIVE
                ), (false), (false)
            )
        )
    }
}

interface MainContentCallback {
    
    fun onTodayChange(today: Boolean)
    fun onTimeFilterClick()
    fun onStyleChange()
    fun onRespond(meet: MeetingModel)
    fun onMeetClick(meet: MeetingModel)
    fun onNavBarSelect(point: Int)
    fun onOpenFiltersBottomSheet()
    fun onCloseAlert()
    
    fun onResetMeets()
    
    fun onMeetMoreClick()
    
    fun meetInteraction(
        direction: DirectionType,
        meet: MeetingModel,
        state: SwipeableCardState,
    )
}

data class MainContentState(
    val grid: Boolean,
    val today: Boolean,
    val selectDate: Boolean,
    val selectTime: Boolean,
    val meetings: List<MeetingModel>,
    val navBarStates: List<NavIconState>,
    val alert: Boolean,
    val hasFilters: Boolean,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainContent(
    state: MainContentState,
    modifier: Modifier = Modifier,
    callback: MainContentCallback? = null,
) {
    Scaffold(modifier.background(colorScheme.background),
        bottomBar = {
            Column(Modifier, Top, CenterHorizontally) {
                // TODO - тут должен быть нормальный ботомщит с фильтрами
                Filters { callback?.onOpenFiltersBottomSheet() }
                NavBar(state.navBarStates)
                { callback?.onNavBarSelect(it) }
            }
        },
        content = { padding ->
            Column {
                TopBar(
                    state.today, state.selectDate,
                    state.selectTime, Modifier,
                    { callback?.onTodayChange(it) }
                ) { callback?.onTimeFilterClick() }
                Use<MainViewModel>(LoadingTrait) {
                    Content(
                        state.grid, state.hasFilters,
                        state.meetings, Modifier
                            .padding(
                                bottom = padding
                                    .calculateBottomPadding() - 28.dp
                            )
                            .padding(horizontal = 16.dp),
                        callback
                    )
                }
            }
        })
    GAlert(
        state.alert, Modifier, Pair(stringResource(R.string.meeting_close_button))
        { callback?.onCloseAlert() },
        stringResource(R.string.complaints_moderate_sen_answer),
        stringResource(R.string.complaints_send_answer),
        { callback?.onCloseAlert() }
    )
}

@Composable
private fun TopBar(
    today: Boolean,
    selectDate: Boolean,
    selectTime: Boolean,
    modifier: Modifier = Modifier,
    onTodayChange: (Boolean) -> Unit,
    onTimeFilterClick: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 10.dp),
        SpaceBetween, CenterVertically
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp),
            Start, Bottom
        ) {
            GiltyString(
                Modifier,
                stringResource(R.string.meeting_profile_bottom_today_label),
                today
            ) { onTodayChange(true) }
            GiltyString(
                Modifier, stringResource(R.string.meeting_profile_bottom_latest_label),
                !today,
            ) { onTodayChange(false) }
        }
        IconButton(onTimeFilterClick) {
            val icons = if(isSystemInDarkTheme())
                listOf(
                    R.drawable.ic_clock_day,
                    R.drawable.ic_clock_indicator_day,
                    R.drawable.ic_calendar_indicator_day,
                    R.drawable.ic_calendar_day
                )
            else listOf(
                R.drawable.ic_clock,
                R.drawable.ic_clock_indicator,
                R.drawable.ic_calendar_indicator,
                R.drawable.ic_calendar
            )
            Image(
                painterResource(
                    when {
                        today && !selectTime -> icons[0]
                        today && selectTime -> icons[1]
                        selectDate -> icons[2]
                        else -> icons[3]
                    }
                ), (null), Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun Content(
    state: Boolean,
    hasFilters: Boolean,
    meetings: List<MeetingModel>,
    modifier: Modifier = Modifier,
    callback: MainContentCallback?,
) {
    Box {
        if(meetings.size <= 2) MeetCard(
            modifier.fillMaxHeight(0.9f),
            EMPTY, hasFilters = hasFilters,
            onMoreClick = { callback?.onMeetMoreClick() },
            onRepeatClick = { callback?.onResetMeets() }
        )
        if(state && meetings.isNotEmpty()) MeetingGridContent(
            modifier
                .background(colorScheme.background)
                .fillMaxSize(), meetings
        ) { callback?.onRespond(it) }
        else {
            MeetingsListContent(
                meetings.map {
                    it to rememberSwipeableCardState()
                }, modifier.fillMaxHeight(0.9f),
                { meet, it -> callback?.meetInteraction(LEFT, meet, it) },
                { meet, it ->
                    callback?.onRespond(meet)
                    callback?.meetInteraction(RIGHT, meet, it)
                }
            ) { callback?.onMeetClick(it) }
        }
    }
}

@Composable
@Suppress("unused")
private fun Floating(
    state: MainContentState,
    callback: MainContentCallback?,
) {
    SquareCheckBox(!state.grid, Modifier)
    { callback?.onStyleChange() }
}

@Composable
private fun Filters(
    modifier: Modifier = Modifier,
    onOpenFilters: () -> Unit,
) {
    Box(modifier, BottomCenter) {
        GDividerBold(
            Modifier
                .width(40.dp)
                .padding(bottom = 22.dp)
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ ->
                        onOpenFilters()
                    }
                }
                .clickable { onOpenFilters() }
        )
    }
}