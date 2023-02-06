package ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.mainscreen.presentation.ui.filter.FiltersBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.calendar.CalendarBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.meet.MeetBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.time.TimeBs
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.mainscreen.viewmodels.FiltersViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.MeetBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

@Composable
fun MainScreen(vm: MainViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val navBar by vm.navBar.collectAsState()
    val today by vm.today.collectAsState()
    val time by vm.time.collectAsState()
    val days by vm.days.collectAsState()
    val grid by vm.grid.collectAsState()
    val meetings by vm.meetings.collectAsState()
    val alert by vm.alert.collectAsState()
    val hasFilters by vm.meetFilters.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getMeets()
    }
    
    Use<MainViewModel>(LoadingTrait) {
        MainContent(
            MainContentState(
                grid, today,
                days.isNotEmpty(),
                time.isNotBlank(),
                meetings, navBar, alert,
                hasFilters.isNotNullOrEmpty()
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
                            Connector<MeetBsViewModel>(vm.scope) {
                                MeetBs(it, meet.id, (false))
                            }
                        }
                    }
                }
                
                override fun onRespond(meet: MeetingModel) {
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<MeetBsViewModel>(vm.scope) {
                                MeetBs(it, meet.id, (true))
                            }
                        }
                    }
                }
                
                override fun onOpenFiltersBottomSheet() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<FiltersViewModel>(vm.scope) {
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
                
                override fun onTodayChange() {
                    scope.launch { vm.changeGroup() }
                }
                
                override fun onStyleChange() {
                    scope.launch { vm.changeGrid() }
                }
            })
    }
}