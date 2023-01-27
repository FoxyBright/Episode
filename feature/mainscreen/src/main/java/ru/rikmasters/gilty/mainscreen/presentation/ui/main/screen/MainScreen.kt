package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.presentation.ui.filter.MeetingFilterContent
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.GCalendarWidget
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.TimeBS
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.rememberSwipeableCardState
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.organizer.OrganizerProfile
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.organizer.OrganizerProfileState
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants.ParticipantsList
import ru.rikmasters.gilty.profile.presentation.ui.bottoms.participants.ParticipantsListCallback
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsContent
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBsState
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType.ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

@Composable
fun MainScreen(nav: NavState = get()) {
    var hiddenPhoto by remember { mutableStateOf(false) }
    var menuState by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val daysList =
        remember { mutableStateListOf<String>() }
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
    var timeSelect by remember { mutableStateOf("") }
    val meetings = remember {
        mutableStateListOf(
            DemoMeetingModel,
            DemoMeetingModel,
            DemoMeetingModel.copy(isOnline = true),
            DemoMeetingModel,
            DemoMeetingModel,
        )
    }
    
    var selectMeet by remember {
        mutableStateOf<MeetingModel?>(null)
    }
    
    var alert by remember { mutableStateOf(false) }
    val cardStates =
        meetings.map { it to rememberSwipeableCardState() }
    
    var clickedMeet by remember {
        mutableStateOf<MeetingModel?>(null)
    }
    
    val meetBSCallback = object: MeetingBsCallback {
        override fun onKebabClick(state: Boolean) {
            menuState = state
        }
        
        override fun onAllMembersClick() {
            scope.launch {
                asm.bottomSheet.expand {
                    selectMeet?.let {
                        ParticipantsList(
                            DemoMeetingModel, DemoMemberModelList,
                            Modifier, object: ParticipantsListCallback {}
                        )
                    }
                }
            }
        }
        
        override fun onMemberClick(member: MemberModel) {
            scope.launch {
                asm.bottomSheet.expand {
                    selectMeet?.let {
                        Organizer(it, asm, scope)
                    }
                }
            }
        }
        
        override fun onAvatarClick() {
            selectMeet?.let {
                scope.launch {
                    asm.bottomSheet.expand {
                        Organizer(it, asm, scope)
                    }
                }
            }
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
                        it.organizer?.avatar?.id
                    }&meetType=${it.category.id}"
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
            grid, today, daysList.isNotEmpty(),
            timeSelect.isNotBlank(), meetings,
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
                    DemoMeetingModel,
                    DemoMeetingModel,
                    DemoMeetingModel.copy(isOnline = true),
                    DemoMeetingModel,
                    DemoMeetingModel,
                ); list.forEach { meetings.add(it) }
            }
            
            override fun onMeetMoreClick() {
            }
            
            override fun onMeetClick(meet: MeetingModel) {
                selectMeet = meet
                clickedMeet = meet
                scope.launch {
                    asm.bottomSheet.expand {
                        MeetingBsContent(
                            MeetingBsState(
                                menuState, meet,
                                DemoMemberModelList,
                                distanceCalculator(meet)
                            ), Modifier
                                .padding(16.dp)
                                .padding(bottom = 40.dp, top = 14.dp),
                            meetBSCallback
                        )
                    }
                }
            }
            
            override fun onTimeFilterClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        if(today) TimeBS {
                            timeSelect = it
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }
                        }
                        else GCalendarWidget(
                            Modifier.padding(16.dp), daysList,
                            {
                                if(daysList.contains(it)) daysList.remove(it)
                                else daysList.add(it)
                            }, {
                                scope.launch {
                                    asm.bottomSheet.collapse()
                                }
                            })
                        { daysList.clear() }
                    }
                }
            }
            
            override fun onCloseAlert() {
                alert = false
            }
            
            override fun onInteresting(
                meet: MeetingModel,
                state: SwipeableCardState,
            ) {
                scope.launch {
                    state.swipe(RIGHT)
                    meetings.remove(meet)
                }
            }
            
            override fun onNotInteresting(
                meet: MeetingModel,
                state: SwipeableCardState,
            ) {
                scope.launch {
                    state.swipe(LEFT)
                    meetings.remove(meet)
                }
            }
            
            override fun onTodayChange() {
                today = !today
            }
            
            override fun onRespond(meet: MeetingModel) {
                selectMeet = meet
                clickedMeet = meet
                scope.launch {
                    asm.bottomSheet.expand {
                        MeetingBsContent(
                            MeetingBsState(
                                menuState, meet,
                                detailed = Pair(
                                    commentText, hiddenPhoto
                                )
                            ), Modifier
                                .padding(16.dp)
                                .padding(bottom = 40.dp, top = 14.dp),
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

@Composable
private fun Meet(
    meet: MeetingModel,
    asm: AppStateModel = get(),
    scope: CoroutineScope,
) {
    val context = LocalContext.current
    var menuState by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf(false) }
    
    MeetingBsContent(
        MeetingBsState(
            menuState, meet,
            DemoMemberModelList,
            distanceCalculator(meet),
            (true), (true), (true)
        ),
        Modifier
            .padding(16.dp)
            .padding(top = 14.dp),
        object: MeetingBsCallback {
            
            override fun closeAlert() {
                alert = false
            }
            
            override fun onKebabClick(state: Boolean) {
                menuState = state
            }
            
            override fun onMenuItemClick(index: Int) {
                menuState = false
                scope.launch {
                    asm.bottomSheet.expand {
                        ComplainsContent(DemoMeetingModel) {
                            scope.launch {
                                asm.bottomSheet.collapse()
                            }; alert = true
                        }
                    }
                }
            }
            
            override fun onAllMembersClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Participants(meet, asm, scope)
                    }
                }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        Organizer(meet, asm, scope)
                    }
                }
            }
            
            override fun onBottomButtonClick(point: Int) {
                when(point) {
                    1 -> Toast.makeText(
                        context,
                        "Вы покинули meet",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    2 -> Toast.makeText(
                        context,
                        "ВААААУ, Типа поделился) Съешь пирожок с полки",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    3 -> Toast.makeText(
                        context,
                        "Вы отменили встречу",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )
}

@Composable
private fun Participants(
    meet: MeetingModel,
    asm: AppStateModel,
    scope: CoroutineScope,
) {
    ParticipantsList(
        DemoMeetingModel, DemoMemberModelList,
        Modifier, callback = object: ParticipantsListCallback {
            override fun onBack() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Meet(meet, asm, scope)
                    }
                }
            }
            
            override fun onMemberClick(member: MemberModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        Organizer(meet, asm, scope)
                    }
                }
            }
        }
    )
}

@Composable
fun Organizer(
    meet: MeetingModel,
    asm: AppStateModel = get(),
    scope: CoroutineScope,
) {
    var observeState by remember { mutableStateOf(false) }
    val currentMeetings = DemoMeetingList
    val profileState = ProfileState(
        DemoProfileModel,
        ORGANIZER,
        observeState = observeState
    )
    OrganizerProfile(
        Modifier, OrganizerProfileState(
            profileState, currentMeetings
        ), object: UserProfileCallback {
            override fun menu(state: Boolean) {}
            override fun closeAlert() {}
            
            override fun onMeetingClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        Meet(meet, asm, scope)
                    }
                }
            }
            
            override fun onBack() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Meet(meet, asm, scope)
                    }
                }
            }
            
            override fun onObserveChange(state: Boolean) {
                super.onObserveChange(observeState)
                observeState = state
            }
        }
    )
}