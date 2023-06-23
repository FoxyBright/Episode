package ru.rikmasters.gilty.mainscreen.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
fun MainContentPreview() {
    GiltyTheme {
        MainContent(
            MainContentState(
                grid = false,
                today = false,
                selectDate = false,
                selectTime = false,
                meetings = DemoMeetingList,
                navBarStates = listOf(
                    INACTIVE, ACTIVE,
                    INACTIVE, NEW_INACTIVE, INACTIVE
                ),
                alert = false,
                hasFilters = false,
                bsState = rememberBottomSheetScaffoldState()
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
                grid = true,
                today = true,
                selectDate = false,
                selectTime = false,
                meetings = DemoMeetingList,
                navBarStates = listOf(
                    INACTIVE, ACTIVE,
                    INACTIVE, NEW_INACTIVE, INACTIVE
                ),
                alert = false,
                hasFilters = false,
                bsState = rememberBottomSheetScaffoldState()
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
    
    fun updateMainScreen()
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
    val smthError: Boolean = true,
)

@Composable
fun MainContent(
    state: MainContentState,
    modifier: Modifier = Modifier,
    callback: MainContentCallback? = null,
) {
    val alpha = state.bsState
        .bottomSheetState.offset.value / 1500
    Filters(state, alpha) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    // if(state.smthError)
                    // Transparent
                    // else
                    colorScheme.background
                )
        ) {
            // if(state.smthError) ErrorInternetConnection {
            // callback?.updateMainScreen()
            // }
            Column {
                TopBar(
                    today = state.today,
                    selectDate = state.selectDate,
                    selectTime = state.selectTime,
                    modifier = Modifier.padding(top = 50.dp),
                    onTodayChange = { callback?.onTodayChange(it) }
                ) { callback?.onTimeFilterClick() }
                Use<MainViewModel>(LoadingTrait) {
                    // if(!state.smthError)
                    Content(
                        state = state.grid,
                        hasFilters = state.hasFilters,
                        meetings = state.meetings,
                        modifier = modifier
                            .fillMaxHeight(0.74f)
                            .padding(horizontal = 16.dp),
                        callback = callback
                    )
                }
            }
        }
    }
    Box(Modifier.fillMaxSize(), BottomCenter) {
        val animate = state.bsState.bottomSheetState
            .isAnimationRunning || state.bsState
            .bottomSheetState.isExpanded
        NavBar(
            state = state.navBarStates,
            modifier = Modifier.alpha(if(animate) alpha else 1f)
        ) { if(!animate) callback?.onNavBarSelect(it) }
    }
    GAlert(
        show = state.alert,
        success = Pair(stringResource(R.string.meeting_close_button)) {
            callback?.onCloseAlert()
        },
        label = stringResource(
            R.string.complaints_moderate_sen_answer
        ),
        title = stringResource(
            R.string.complaints_send_answer
        ),
        onDismissRequest = {
            callback?.onCloseAlert()
        }
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
        ) { TodayToggle(today) { onTodayChange(it) } }
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
                painter = painterResource(
                    when {
                        today && !selectTime -> icons[0]
                        today && selectTime -> icons[1]
                        selectDate -> icons[2]
                        else -> icons[3]
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
@SuppressLint("CoroutineCreationDuringComposition")
private fun TodayToggle(
    today: Boolean,
    modifier: Modifier = Modifier,
    onTodayChange: (Boolean) -> Unit,
) {
    val tween = 300
    val todayColor = animateColorAsState(
        targetValue = if(today) colorScheme.tertiary
        else colorScheme.onTertiary,
        animationSpec = tween(tween)
    ).value
    val afterColor = animateColorAsState(
        targetValue = if(today) colorScheme.onTertiary
        else colorScheme.tertiary,
        animationSpec = tween(tween)
    ).value
    var todayWeight by remember {
        mutableStateOf(110.dp)
    }
    var afterWeight by remember {
        mutableStateOf(52.dp)
    }
    
    LaunchedEffect(today) {
        launch {
            listOf(72f, 110f)
                .let { if(today) it else it.reversed() }
                .let {
                    animate(
                        it.first(), it.last(),
                        animationSpec = tween(tween)
                    ) { value, _ ->
                        todayWeight = value.toInt().dp
                    }
                }
        }
        launch {
            listOf(79f, 52f)
                .let { if(today) it else it.reversed() }
                .let {
                    animate(
                        it.first(), it.last(),
                        animationSpec = tween(tween)
                    ) { value, _ ->
                        afterWeight = value.toInt().dp
                    }
                }
        }
    }
    
    Row(modifier, Start, Bottom) {
        Icon(
            painter = painterResource(
                R.drawable.ic_today_title_max
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .width(todayWeight)
                .offset()
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { onTodayChange(true) },
            tint = todayColor
        )
        Icon(
            painter = painterResource(
                R.drawable.ic_after_title_max
            ),
            modifier = Modifier
                .width(afterWeight)
                .offset(
                    y = animateDpAsState(
                        if(today) -(3).dp
                        else 0.dp,
                        tween(tween)
                    ).value
                )
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { onTodayChange(false) },
            contentDescription = null,
            tint = afterColor
        )
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
        if(meetings.size < 2) MeetCard(
            modifier = modifier.padding(horizontal = 1.dp),
            type = EMPTY,
            stack = false,
            hasFilters = hasFilters,
            onMoreClick = { callback?.onMeetMoreClick() },
            onRepeatClick = { callback?.onResetMeets() }
        )
        val states =
            meetings.map { it to rememberSwipeableCardState() }
        MeetingsListContent(
            states = states,
            modifier = modifier.padding(top = 24.dp),
            notInteresting = { meet, it ->
                callback?.meetInteraction(LEFT, meet, it)
            },
            onSelect = { meet, it ->
                callback?.onRespond(meet)
                callback?.meetInteraction(RIGHT, meet, it)
            }
        ) { callback?.onMeetClick(it) }
    }
}

@Composable
@Suppress("unused")
private fun Floating(
    state: MainContentState,
    callback: MainContentCallback?,
) {
    SquareCheckBox(!state.grid)
    { callback?.onStyleChange() }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Filters(
    state: MainContentState,
    alpha: Float,
    content: @Composable () -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = {
            Filters(
                isCollapsed = (state.bsState
                    .bottomSheetState
                    .offset.value > screenHeight / 2),
                alpha = alpha,
                scope = state.vmScope
            )
        },
        scaffoldState = state.bsState,
        sheetShape = shapes.bigTopShapes,
        sheetBackgroundColor = Transparent,
        sheetPeekHeight = 126.dp
    ) { content.invoke() }
}

@Composable
private fun Filters(
    isCollapsed: Boolean,
    alpha: Float,
    scope: Scope?,
) {
    scope?.let {
        Box {
            Box(
                Modifier
                    .offset(y = gripOffset(isCollapsed))
                    .clip(shapes.bigTopShapes)
            ) {
                Connector<FiltersBsViewModel>(scope) {
                    FiltersBs(it, alpha, isCollapsed)
                }
            }
            Grip(Modifier.align(TopCenter), !isCollapsed)
        }
    } ?: return
}

@Composable
private fun gripOffset(
    state: Boolean,
    offset: Dp = 12.dp,
) = animateDpAsState(
    if(state) offset else 0.dp
).value

@Composable
private fun Grip(
    modifier: Modifier,
    state: Boolean,
) {
    @Composable
    fun gripOffset(
        state: Boolean,
        offset: Dp = 12.dp,
    ) = animateDpAsState(
        if(state) offset else 0.dp
    ).value
    Column(
        modifier, Top,
        CenterHorizontally
    ) {
        Spacer(
            Modifier.height(
                gripOffset(state, 8.dp)
            )
        )
        Box(
            Modifier
                .background(
                    colors.gripColor,
                    CircleShape
                )
                .size(40.dp, 5.dp)
                .padding(
                    bottom = gripOffset(state)
                )
        )
    }
}