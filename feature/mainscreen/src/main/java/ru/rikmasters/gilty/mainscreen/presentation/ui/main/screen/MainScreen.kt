package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.shared.common.MeetingDetailsBottomCallback
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

@Composable
fun MainScreen(nav: NavState = get()) {
    var hiddenPhoto by remember { mutableStateOf(false) }
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
    val cardStates =
        meetings.map { it to rememberSwipeableCardState() }
    MainContent(
        MainContentState(
            grid, switcher, meetings, cardStates, stateList
        ), Modifier, object : MainContentCallback {
            override fun onNavBarSelect(point: Int) {
                repeat(stateList.size) {
                    if (it == point) stateList[it] = ACTIVE
                    else if (stateList[it] != NEW)
                        stateList[it] = INACTIVE
                }
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
                        Meeting(meet, hiddenPhoto, commentText,
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
                        CategoriesScreen() // TODO фильтры встречи
                    }
                }
            }

            override fun onStyleChange() {
                grid = !grid
            }
        })
}