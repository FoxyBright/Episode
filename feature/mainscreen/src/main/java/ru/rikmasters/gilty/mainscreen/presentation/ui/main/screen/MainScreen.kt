package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.presentation.ui.filter.MeetingFilterContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBSCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBSState
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBottomSheet
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType.*
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.*

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
    
    var today by remember {
        mutableStateOf(true)
    }
    val meetings = remember {
        mutableStateListOf(
            getDemoMeetingModel(category = ART),
            getDemoMeetingModel(category = ENTERTAINMENT, isOnline = true),
            getDemoMeetingModel(category = BUSINESS),
            getDemoMeetingModel(category = MASTER_CLASSES),
            getDemoMeetingModel(category = EROTIC)
        )
    }
    
    val context = LocalContext.current
    var alert by remember { mutableStateOf(false) }
    val cardStates =
        meetings.map { it to rememberSwipeableCardState() }
    
    var clickedMeet by remember {
        mutableStateOf<MeetingModel?>(null)
    }
    
    val meetBSCallback = object: MeetingBSCallback {
        override fun onKebabClick(state: Boolean) {
            menuState = state
        }
        
        override fun onMenuItemClick(index: Int) {
            scope.launch {
                asm.bottomSheet.expand {
                    menuState = false
                    clickedMeet?.let {
                        ComplainsContent(it) {
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }; alert = true
                        }
                    }
                }
            }
        }
        
        override fun onBottomButtonClick(point: Int) {
            clickedMeet?.let {
                nav.navigateAbsolute(
                    "main/reaction?avatar=${
                        it.organizer.avatar.id
                    }&meetType=${it.category.ordinal}"
                ); scope.launch { asm.bottomSheet.collapse() }
            }
        }
        
        
        override fun onHiddenPhotoActive(hidden: Boolean) {
            hiddenPhoto = hidden
        }
        
        override fun onCommentChange(text: String) {
            commentText = text
        }
        
        override fun onCommentTextClear() {
            commentText = ""
        }
    }
    
    MainContent(
        MainContentState(
            grid, today, meetings,
            cardStates, stateList, alert
        ), Modifier, object: MainContentCallback {
            override fun onNavBarSelect(point: Int) {
                repeat(stateList.size) {
                    if(it == point) stateList[it] = ACTIVE
                    else if(stateList[it] != NEW)
                        stateList[it] = INACTIVE
                    when(point) {
                        0 -> nav.navigateAbsolute("main/meetings")
                        1 -> nav.navigateAbsolute("notification/list")
                        2 -> nav.navigateAbsolute("addmeet/category")
                        3 -> nav.navigateAbsolute("chats/main")
                        4 -> nav.navigateAbsolute("profile/main")
                    }
                }
            }
            
            override fun onMeetsRepeatClick() {
                val list = listOf(
                    getDemoMeetingModel(category = ART),
                    getDemoMeetingModel(category = ENTERTAINMENT, isOnline = true),
                    getDemoMeetingModel(category = BUSINESS),
                    getDemoMeetingModel(category = MASTER_CLASSES),
                    getDemoMeetingModel(category = EROTIC)
                ); list.forEach { meetings.add(it) }
            }
            
            override fun onMeetMoreClick() {
            }
            
            override fun onMeetClick(meet: MeetingModel) {
                clickedMeet = meet
                scope.launch {
                    asm.bottomSheet.expand {
                        MeetingBottomSheet(
                            MeetingBSState(
                                menuState, meet,
                                DemoMemberModelList,
                                distanceCalculator(meet)
                            ), Modifier
                                .padding(16.dp)
                                .padding(bottom = 40.dp),
                            meetBSCallback
                        )
                    }
                }
            }
            
            override fun onTimeFilterClick() {
                Toast.makeText(
                    context, "Тут будет фильтрация по дате и времени",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onCloseAlert() {
                alert = false
            }
            
            override fun onInteresting(
                meet: MeetingModel,
                state: SwipeableCardState
            ) {
                scope.launch { state.swipe(RIGHT) }
                meetings.remove(meet)
            }
            
            override fun onNotInteresting(
                meet: MeetingModel,
                state: SwipeableCardState
            ) {
                scope.launch { state.swipe(LEFT) }
                meetings.remove(meet)
            }
            
            override fun onTodayChange() {
                today = !today
            }
            
            override fun onRespond(meet: MeetingModel) {
                clickedMeet = meet
                scope.launch {
                    asm.bottomSheet.expand {
                        MeetingBottomSheet(
                            MeetingBSState(
                                menuState, meet,
                                detailed = Pair(
                                    commentText, hiddenPhoto
                                )
                            ), Modifier
                                .padding(16.dp)
                                .padding(bottom = 40.dp),
                            meetBSCallback
                        )
                    }
                }
            }
            
            override fun onOpenFiltersBottomSheet() {
                scope.launch {
                    asm.bottomSheet.expand {
                        MeetingFilterContent {
                            scope.launch { asm.bottomSheet.collapse() }
                        }
                    }
                }
            }
            
            override fun onStyleChange() {
                grid = !grid
            }
        })
}
