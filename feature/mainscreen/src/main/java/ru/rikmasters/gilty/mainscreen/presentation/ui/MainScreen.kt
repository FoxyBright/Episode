package ru.rikmasters.gilty.mainscreen.presentation.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.SHORT_MEET
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar.CalendarBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.time.TimeBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.filter.FiltersBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun MainScreen(vm: MainViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val meetings by vm.meetings.collectAsState()
    val navBar by vm.navBar.collectAsState()
    val days by vm.days.collectAsState()
    val alert by vm.alert.collectAsState()
    val today by vm.today.collectAsState()
    val grid by vm.grid.collectAsState()
    val time by vm.time.collectAsState()
    
    val hasFilters = vm
        .meetFilters
        .collectAsState()
        .value
        .isNotNullOrEmpty()
    
    LaunchedEffect(Unit) {
        vm.getChatStatus()
        vm.getMeets()
        vm.getAllCategories()
        vm.getUserCategories()
    }
    
    MainContent(
        MainContentState(
            grid, today, days.isNotEmpty(),
            time.isNotBlank(), meetings,
            navBar, alert, hasFilters
        ), Modifier, object: MainContentCallback {
            
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
            
            override fun onOpenFiltersBottomSheet() {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<FiltersBsViewModel>(vm.scope) {
                            FiltersBs(it)
                        }
                    }
                }
            }
            
            override fun onTimeFilterClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        if(today) Connector<TimeBsViewModel>(vm.scope) {
                            TimeBs(it)
                        } else Connector<CalendarBsViewModel>(vm.scope) {
                            CalendarBs(it)
                        }
                    }
                }
            }
            
            override fun meetInteraction(
                direction: DirectionType,
                meet: MeetingModel,
                state: SwipeableCardState,
            ) {
                scope.launch { vm.meetInteraction(direction, meet, state) }
            }
            
            override fun onMeetMoreClick() {
                scope.launch { vm.moreMeet() }
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