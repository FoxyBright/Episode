package ru.rikmasters.gilty.mainscreen.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.mainscreen.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.grid.MeetingGridContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.swipe.MeetingsListContent
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.DividerBold
import ru.rikmasters.gilty.shared.shared.GiltyString
import ru.rikmasters.gilty.shared.shared.SquareCheckBox
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
fun MainContentPreview() {
    GiltyTheme {
        MainContent(
            MainContentState(true, listOf(), DemoMeetingList, rememberCoroutineScope())
        )
    }
}

interface MainContentCallback {
    fun onTodayChange() {}
    fun onTimeFilterClick() {}
    fun onStyleChange() {}
    fun onMeetingSelect() {}
}

data class MainContentState(
    val grid: Boolean,
    val switcher: List<Boolean>,
    val meetings: List<ShortMeetingModel>,
    val scope: CoroutineScope
)

@Composable
fun MainContent(
    state: MainContentState,
    modifier: Modifier = Modifier,
    callback: MainContentCallback? = null,
    asm: AppStateModel = get()
) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.82f)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 10.dp),
            Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                GiltyString(
                    Modifier.padding(end = 12.dp),
                    stringResource(R.string.meeting_profile_bottom_today_label),
                    state.switcher.first()
                ) { callback?.onTodayChange() }
                GiltyString(
                    Modifier,
                    stringResource(R.string.meeting_profile_bottom_latest_label),
                    state.switcher.last()
                ) { callback?.onTodayChange() }
            }
            IconButton({ callback?.onTimeFilterClick() }) {
                Icon(
                    if (state.switcher.first())
                        painterResource(R.drawable.ic_clock)
                    else painterResource(R.drawable.ic_calendar),
                    null,
                    Modifier.size(30.dp),
                    ThemeExtra.colors.mainTextColor
                )
            }
        }
        if (state.grid) MeetingGridContent(
            Modifier.padding(16.dp),
            state.meetings
        ) { callback?.onMeetingSelect() }
        else MeetingsListContent(state.meetings)
    }
    Box(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(ThemeExtra.colors.cardBackground)
                .align(Alignment.BottomCenter),
            Arrangement.SpaceEvenly, Alignment.CenterVertically
        ) {
            repeat(5) {
                Icon(
                    painterResource(R.drawable.ic_clock),
                    null, Modifier.size(30.dp)
                )
            }
        }
        DividerBold(
            Modifier
                .padding(horizontal = 180.dp)
                .padding(bottom = 92.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter)
                .clickable {
                    state.scope.launch {
                        asm.bottomSheetState.halfExpand {
                            CategoriesScreen()
                        }
                    }
                }
        )
        SquareCheckBox(
            state.grid,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 92.dp)
        ) { callback?.onStyleChange() }
    }
}
