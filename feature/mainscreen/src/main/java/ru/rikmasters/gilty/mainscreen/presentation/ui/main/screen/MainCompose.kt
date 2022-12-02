package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.grid.MeetingGridContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe.MeetingsListContent
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.MeetingBottomSheetTopBarCompose
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomCallback
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomCompose
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomComposeState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.DividerBold
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GiltyString
import ru.rikmasters.gilty.shared.shared.NavBar
import ru.rikmasters.gilty.shared.shared.SquareCheckBox
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
                    if (state.switcher.first)
                        painterResource(R.drawable.ic_clock)
                    else painterResource(R.drawable.ic_calendar),
                    null,
                    Modifier.size(30.dp),
                    MaterialTheme.colorScheme.tertiary
                )
            }
        }
        if (state.grid)
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
                { callback?.notInteresting(it) }
            ) { meet, it ->
                callback?.onRespond(meet)
                callback?.interesting(it)
            }
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

@Composable
fun Meeting(
    menuState: Boolean,
    menuCollapse: ((Boolean) -> Unit)? = null,
    menuItemClick: ((Int) -> Unit)? = null,
    meet: FullMeetingModel,
    hiddenPhoto: Boolean,
    commentText: String,
    callback: MeetingDetailsBottomCallback?
) {
    Column(
        Modifier
            .padding(16.dp)
            .padding(bottom = 40.dp)
    ) {
        MeetingBottomSheetTopBarCompose(
            Modifier, meet, meet.duration,
            menuState, { menuCollapse?.let { c -> c(it) } },
            { menuItemClick?.let { c -> c(it) } }
        )
        MeetingDetailsBottomCompose(
            Modifier.padding(16.dp),
            MeetingDetailsBottomComposeState(hiddenPhoto, commentText),
            callback
        )
    }
}