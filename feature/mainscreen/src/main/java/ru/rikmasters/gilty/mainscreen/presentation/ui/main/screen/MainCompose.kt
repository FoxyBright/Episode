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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import ru.rikmasters.gilty.shared.common.MeetingBottomSheetTopBarCompose
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomCallback
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomCompose
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomComposeState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.shared.DividerBold
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
                true,
                listOf(),
                DemoMeetingList,
                rememberCoroutineScope(),
                listOf(
                    NavIconState.INACTIVE,
                    NavIconState.ACTIVE,
                    NavIconState.INACTIVE,
                    NavIconState.NEW,
                    NavIconState.INACTIVE
                )
            )
        )
    }
}

interface MainContentCallback {
    fun onTodayChange() {}
    fun onTimeFilterClick() {}
    fun onStyleChange() {}
    fun onRespond(avatar: String) {}
    fun onNavBarSelect(point: Int) {}
    fun openFiltersBottomSheet(){}
}

data class MainContentState(
    val grid: Boolean,
    val switcher: List<Boolean>,
    val meetings: List<FullMeetingModel>,
    val scope: CoroutineScope,
    val navBarStates: List<NavIconState>
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
            ) {
                state.scope.launch {
                    asm.bottomSheetState.expand {
                        Meeting(it) {
                            callback?.onRespond(it.organizer.avatar.id)
                            state.scope.launch { asm.bottomSheetState.collapse() }
                        }
                    }
                }
            }
        else MeetingsListContent(
            state.meetings, state.scope,
            Modifier.fillMaxHeight(0.84f)
        ) {
            state.scope.launch {
                asm.bottomSheetState.expand {
                    Meeting(it) {
                        callback?.onRespond(it.organizer.avatar.id)
                        state.scope.launch { asm.bottomSheetState.collapse() }
                    }
                }
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
                .clickable { callback?.openFiltersBottomSheet()}
        )
        SquareCheckBox(
            state.grid,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 92.dp)
        ) { callback?.onStyleChange() }
    }
}

@Composable
fun Meeting(meet: FullMeetingModel, onRespond: () -> Unit) {
    Column(
        Modifier
            .padding(16.dp)
            .padding(bottom = 50.dp)
    ) {
        MeetingBottomSheetTopBarCompose(
            Modifier, meet, meet.duration
        )
        var hiddenPhoto by remember { mutableStateOf(false) }
        var commentText by remember { mutableStateOf("") }
        MeetingDetailsBottomCompose(
            Modifier.padding(16.dp),
            MeetingDetailsBottomComposeState(hiddenPhoto, commentText),
            object : MeetingDetailsBottomCallback {
                override fun onHiddenPhotoActive(hidden: Boolean) {
                    hiddenPhoto = hidden
                }

                override fun onCommentChange(text: String) {
                    commentText = text
                }

                override fun onRespondClick() {
                    onRespond()
                }
            }
        )
    }
}