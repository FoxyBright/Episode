package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.grid.MeetingGridContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe.MeetingsListContent
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_calendar
import ru.rikmasters.gilty.shared.R.drawable.ic_clock
import ru.rikmasters.gilty.shared.R.string.complaints_moderate_sen_answer
import ru.rikmasters.gilty.shared.R.string.complaints_send_answer
import ru.rikmasters.gilty.shared.R.string.meeting_close_button
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
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
    fun onRespond(meet: MeetingModel) {}
    fun onMeetClick(meet: MeetingModel) {}
    fun onNavBarSelect(point: Int) {}
    fun openFiltersBottomSheet() {}
    fun interesting(state: SwipeableCardState) {}
    fun notInteresting(state: SwipeableCardState) {}
    fun closeAlert()
}

data class MainContentState(
    val grid: Boolean,
    val switcher: Pair<Boolean, Boolean>,
    val meetings: List<MeetingModel>,
    val cardStates: List<Pair<MeetingModel, SwipeableCardState>>,
    val navBarStates: List<NavIconState>,
    val alert: Boolean,
)

@Composable
fun MainContent(
    state: MainContentState,
    modifier: Modifier = Modifier,
    callback: MainContentCallback? = null,
) {
    Column(modifier.background(colorScheme.background)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 10.dp),
            SpaceBetween
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Bottom
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
            IconButton(
                { callback?.onTimeFilterClick() }
            ) {
                Icon(
                    if(state.switcher.first)
                        painterResource(ic_clock)
                    else painterResource(ic_calendar),
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
            Modifier.align(BottomCenter)
        ) { callback?.onNavBarSelect(it) }
        DividerBold( // TODO - тут должен быть нормальный ботомщит с фильтрами
            Modifier
                .padding(horizontal = 180.dp)
                .padding(bottom = 92.dp)
                .clip(CircleShape)
                .align(BottomCenter)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ ->
                        callback?.openFiltersBottomSheet()
                    }
                }
                .clickable { callback?.openFiltersBottomSheet() }
        )
        SquareCheckBox(
            !state.grid, Modifier
                .align(BottomEnd)
                .padding(end = 16.dp, bottom = 92.dp)
        ) { callback?.onStyleChange() }
    }
    GAlert(
        state.alert, { callback?.closeAlert() },
        stringResource(complaints_send_answer),
        label = stringResource(complaints_moderate_sen_answer),
        success = Pair(stringResource(meeting_close_button)) { callback?.closeAlert() }
    )
}