package ru.rikmasters.gilty.mainscreen.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.auth.meetings.MeetFilters
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource.MeetFilterGroup.Companion.get
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

class MainViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _today = MutableStateFlow(true)
    val today = _today.asStateFlow()
    
    private val _grid = MutableStateFlow(false)
    val grid = _grid.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _days = MutableStateFlow(emptyList<String>())
    val days = _days.asStateFlow()
    
    private val _time = MutableStateFlow("")
    val time = _time.asStateFlow()
    
    private val navBarStateList = listOf(
        ACTIVE, NEW, INACTIVE, NEW, INACTIVE
    )
    
    private val _meetings = MutableStateFlow(emptyList<MeetingModel>())
    val meetings = _meetings.asStateFlow()
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private val _meetFilters = MutableStateFlow(MeetFilters())
    private val meetFilters = _meetFilters.asStateFlow()
    
    suspend fun getMeets() {
        _meetings.emit(
            meetManager.getMeetings(
                meetFilters.value.copy(
                    group = get(
                        today.value.compareTo(false)
                    )
                )
            )
        )
    }
    
    suspend fun alertDismiss() {
        _alert.emit(!alert.value)
    }
    
    suspend fun repeatMeets() {
        getMeets()
    }
    
    suspend fun meetInteraction(
        direction: DirectionType,
        meet: MeetingModel,
        state: SwipeableCardState,
    ) {
        state.swipe(direction)
        _meetings.emit(meetings.value - meet)
    }
    
    private suspend fun navBarSetStates(
        states: List<NavIconState>,
    ) {
        _navBar.emit(states)
    }
    
    suspend fun changeGrid() {
        _grid.emit(!grid.value)
    }
    
    suspend fun moreMeet() {
        makeToast("Пока не имеет функционала")
    }
    
    suspend fun navBarNavigate(point: Int): String {
        val list = arrayListOf<NavIconState>()
        repeat(navBar.value.size) {
            list.add(
                when {
                    navBar.value[it] == NEW -> NEW
                    it == point -> ACTIVE
                    else -> INACTIVE
                }
            )
        }
        navBarSetStates(list)
        return when(point) {
            1 -> "notification/list"
            2 -> "addmeet/category"
            3 -> "chats/main"
            4 -> "profile/main"
            else -> "main/meetings"
        }
    }
    
    suspend fun changeGroup() {
        _today.emit(!today.value)
        getMeets()
    }
}