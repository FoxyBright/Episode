package ru.rikmasters.gilty.mainscreen.presentation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.core.scope.Scope
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.mainscreen.presentation.ui.filter.FiltersBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
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
import ru.rikmasters.gilty.shared.shared.bottomsheet.*
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
                ), (false), (false),
                rememberBottomSheetScaffoldState()
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
                DemoMeetingList,
                listOf(
                    INACTIVE, ACTIVE,
                    INACTIVE, NEW_INACTIVE, INACTIVE
                ), (false), (false),
                rememberBottomSheetScaffoldState()
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
    val bsState: BottomSheetScaffoldState,
    val vmScope: Scope? = null,
)

@Composable
fun MainContent(
    state: MainContentState,
    modifier: Modifier = Modifier,
    callback: MainContentCallback? = null,
) {
    Filters(Modifier, state) {
        Column(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TopBar(
                state.today, state.selectDate,
                state.selectTime, Modifier.padding(top = 50.dp),
                { callback?.onTodayChange(it) }
            ) { callback?.onTimeFilterClick() }
            Use<MainViewModel>(LoadingTrait) {
                Content(
                    state.grid, state.hasFilters,
                    state.meetings, modifier
                        .fillMaxHeight(0.74f)
                        .padding(horizontal = 16.dp),
                    callback
                )
            }
        }
    }
    Box(Modifier.fillMaxSize(), BottomCenter) {
        NavBar(state.navBarStates) {
            callback?.onNavBarSelect(it)
        }
    }
    GAlert(
        state.alert,
        Modifier,
        Pair(stringResource(R.string.meeting_close_button))
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
        modifier.fillMaxWidth(),
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
                Modifier,
                stringResource(R.string.meeting_profile_bottom_latest_label),
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
            modifier,
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
                },
                modifier.padding(top = 24.dp),
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
@OptIn(ExperimentalMaterial3Api::class)
private fun Filters(
    modifier: Modifier = Modifier,
    state: MainContentState,
    content: @Composable () -> Unit,
) {
    val alpha = state.bsState
        .bottomSheetState.offset
        .value / 1500
    BottomSheetScaffold(
        sheetContent = {
            state.vmScope?.let { scope ->
                Connector<FiltersBsViewModel>(scope) {
                    FiltersBs(it, alpha)
                }
            }
        }, modifier = modifier,
        scaffoldState = state.bsState,
        sheetShape = RoundedCornerShape(
            topStart = 24.dp, topEnd = 24.dp
        ), sheetBackgroundColor = colorScheme.primaryContainer,
        sheetPeekHeight = 114.dp
    ) { content.invoke() }
}