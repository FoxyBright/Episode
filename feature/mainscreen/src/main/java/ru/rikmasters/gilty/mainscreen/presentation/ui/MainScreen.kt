package ru.rikmasters.gilty.mainscreen.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.SHORT_MEET
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.internetCheck
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.core.data.source.SharedPrefListener.Companion.listenPreference
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.util.composable.getActivity
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar.CalendarBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.time.TimeBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetState
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue
import ru.rikmasters.gilty.shared.shared.bottomsheet.rememberBottomSheetScaffoldState

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MainScreen(vm: MainViewModel) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val activity = getActivity()
    val nav = get<NavState>()
    
    val unreadNotifications by vm.unreadNotifications.collectAsState()
    val unreadMessages by vm.unreadMessages.collectAsState()
    val location by vm.location.collectAsState()
    val meetings by vm.meetings.collectAsState()
    val days by vm.days.collectAsState()
    val today by vm.today.collectAsState()
    val alert by vm.alert.collectAsState()
    val grid by vm.grid.collectAsState()
    val time by vm.time.collectAsState()
    
    val navBar = remember {
        mutableListOf(
            ACTIVE, unreadNotifications,
            INACTIVE, unreadMessages, INACTIVE
        )
    }
    
    val hasFilters = vm
        .meetFilters
        .collectAsState()
        .value
        .isNotNullOrEmpty()
    
    val meetBsState by asm.bottomSheet
        .current.collectAsState()
    LaunchedEffect(meetBsState) {
        if(meetBsState == COLLAPSED)
            vm.resetMeets()
    }
    
    LaunchedEffect(Unit) {
        vm.getAllCategories()
        vm.getUserCategories()
        vm.getUnread()
        vm.getMeets()
        vm.getLocation(activity)
        context.listenPreference("unread_messages", 0)
        { scope.launch { vm.setUnreadMessages(it > 0) } }
        context.listenPreference("unread_notification", 0)
        { scope.launch { vm.setUnreadNotifications(it > 0) } }
    }
    
    val bsState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    
    LaunchedEffect(location) { vm.getMeets() }
    
    var errorState by remember {
        mutableStateOf(false)
    }
    
    scope.launch {
        while(true) {
            delay(500)
            internetCheck(context).let {
                if(!it) errorState = true
            }
        }
    }
    
    MainContent(
        state = MainContentState(
            grid = grid,
            today = today,
            selectDate = days.isNotEmpty(),
            selectTime = time.isNotBlank(),
            meetings = meetings,
            navBarStates = navBar,
            alert = alert,
            hasFilters = hasFilters,
            bsState = bsState,
            vmScope = vm.scope,
            smthError = errorState
        ),
        callback = object: MainContentCallback {
            override fun updateMainScreen() {
                errorState = !internetCheck(context)
                if(!errorState) scope.launch {
                    vm.resetMeets()
                }
            }
            
            override fun onNavBarSelect(point: Int) {
                if(point == 0) return
                scope.launch {
                    nav.navigateAbsolute(
                        vm.navBarNavigate(point)
                    )
                }
            }
            
            override fun onMeetClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(vm.scope, MEET, meet.id)
                    }
                }
            }
            
            override fun onRespond(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(vm.scope, SHORT_MEET, meet.id)
                    }
                }
            }
            
            override fun onTimeFilterClick() {
                if(today) vm.scope.openBS<TimeBsViewModel>(
                    scope = scope,
                    content = { TimeBs(it) }
                )
                else vm.scope.openBS<CalendarBsViewModel>(
                    scope = scope,
                    content = { CalendarBs(it) }
                )
            }
            
            override fun meetInteraction(
                direction: DirectionType,
                meet: MeetingModel,
                state: SwipeableCardState,
            ) {
                scope.launch {
                    vm.meetInteraction(
                        direction, meet, state
                    )
                }
            }
            
            override fun onMeetMoreClick() {
                scope.launch {
                    vm.moreMeet()
                    vm.filterCleaner.emit(
                        !vm.filterCleaner.value
                    )
                }
            }
            
            override fun onResetMeets() {
                scope.launch { vm.resetMeets() }
            }
            
            override fun onCloseAlert() {
                scope.launch { vm.alertDismiss(false) }
            }
            
            override fun onTodayChange(today: Boolean) {
                scope.launch { vm.changeGroup(today) }
            }
            
            override fun onStyleChange() {
                scope.launch { vm.changeGrid() }
            }
        }
    )
}