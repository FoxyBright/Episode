package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.presentation.ui.filter.MeetingFilterContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomCallback
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

@Composable
fun MainScreen(nav: NavState = get()) {
    var hiddenPhoto by remember { mutableStateOf(false) }
    var menuState by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    var grid by remember {
        mutableStateOf(false)
    }
    val stateList = remember {
        mutableStateListOf(
            ACTIVE, NEW, INACTIVE,
            NEW, INACTIVE
        )
    }
    var switcher by remember {
        mutableStateOf(Pair(true, false))
    }
    val meetings = DemoMeetingList
    val context = LocalContext.current
    var alert by remember { mutableStateOf(false) }
    val cardStates =
        meetings.map { it to rememberSwipeableCardState() }
    MainContent(
        MainContentState(
            grid, switcher, meetings,
            cardStates, stateList, alert
        ), Modifier, object : MainContentCallback {
            override fun onNavBarSelect(point: Int) {
                repeat(stateList.size) {
                    if (it == point) stateList[it] = ACTIVE
                    else if (stateList[it] != NEW)
                        stateList[it] = INACTIVE
                    when (point) {
                        0 -> nav.navigateAbsolute("main/meetings")
                        1 -> nav.navigateAbsolute("notification/list")
                        2 -> nav.navigateAbsolute("addmeet/category")
                        3 -> nav.navigateAbsolute("chats/main")
                        4 -> nav.navigateAbsolute("profile/main")
                    }
                }
            }

            override fun onTimeFilterClick() {
                Toast.makeText(
                    context, "Тут будет фильтрация по дате и времени",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun closeAlert() {
                alert = false
            }

            override fun interesting(state: SwipeableCardState) {
                scope.launch { state.swipe(DirectionType.RIGHT) }
            }

            override fun notInteresting(state: SwipeableCardState) {
                scope.launch { state.swipe(DirectionType.LEFT) }
            }

            override fun onTodayChange() {
                switcher = Pair(!switcher.first, !switcher.second)
            }

            override fun onRespond(meet: FullMeetingModel) {
                scope.launch {
                    asm.bottomSheetState.expand {
                        Meeting(menuState,
                            { menuState = it },
                            {
                                menuState = false
                                scope.launch {
                                    asm.bottomSheetState.expand {
                                        Box {
                                            ComplainsContent(DemoFullMeetingModel) {
                                                scope.launch {
                                                    asm.bottomSheetState.collapse()
                                                }; alert = true
                                            }
                                        }
                                    }
                                }
                            },
                            meet, hiddenPhoto, commentText,
                            object : MeetingDetailsBottomCallback {
                                override fun onHiddenPhotoActive(hidden: Boolean) {
                                    hiddenPhoto = hidden
                                }

                                override fun onCommentChange(text: String) {
                                    commentText = text
                                }

                                override fun onRespondClick() {
                                    nav.navigateAbsolute(
                                        "main/reaction?avatar=${meet.organizer.avatar.id}"
                                    ); scope.launch { asm.bottomSheetState.collapse() }
                                }
                            })
                    }
                }
            }

            override fun openFiltersBottomSheet() {
                scope.launch {
                    asm.bottomSheetState.halfExpand {
                        MeetingFilterContent() {
                            scope.launch {
                                asm.bottomSheetState.collapse()
                            }
                        }
                    }
                }
            }

            override fun onStyleChange() {
                grid = !grid
            }
        })
}