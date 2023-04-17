package ru.rikmasters.gilty.mainscreen.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.MeetFilterGroupType.Companion.get
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW_INACTIVE
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.MeetFiltersModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

class MainViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    private val chatManager by inject<ChatManager>()
    
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
    
    private val _meetings = MutableStateFlow(emptyList<MeetingModel>())
    val meetings = _meetings.asStateFlow()
    
    private val _categories = MutableStateFlow(
        emptyList<CategoryModel>()
    )
    val categories = _categories.asStateFlow()
    
    private val _userCategories = MutableStateFlow(
        emptyList<CategoryModel>()
    )
    val userCategories = _userCategories.asStateFlow()
    
    private val _navBar = MutableStateFlow(
        listOf(ACTIVE, INACTIVE, INACTIVE, INACTIVE, INACTIVE)
    )
    val navBar = _navBar.asStateFlow()
    
    private val _meetFilters = MutableStateFlow(MeetFiltersModel())
    val meetFilters = _meetFilters.asStateFlow()
    
    suspend fun getAllCategories() {
        _categories.emit(meetManager.getCategoriesList())
    }
    
    suspend fun getUserCategories() {
        _userCategories.emit(
            profileManager.getUserCategories()
        )
    }
    
    suspend fun getChatStatus() {
        chatManager.getChatsStatus().let {
            if(it > 0) _navBar.emit(
                listOf(
                    ACTIVE, INACTIVE,
                    INACTIVE, NEW_INACTIVE,
                    INACTIVE
                )
            )
        }
    }
    
    suspend fun changeTime(time: String) {
        _time.emit(time)
        getMeets()
    }
    
    suspend fun selectDays(days: List<String>) {
        _days.emit(days)
        getMeets()
    }
    
    suspend fun setFilters(filters: MeetFiltersModel) {
        _meetFilters.emit(
            filters.copy(
                group = get(today.value.compareTo(false)),
                dates = days.value.ifEmpty { null },
                time = time.value.ifBlank { null }
            )
        )
    }
    
    suspend fun getMeets() = singleLoading {
        _meetFilters.emit(
            meetFilters.value.copy(
                group = get(today.value.compareTo(false)),
                dates = days.value.ifEmpty { null },
                time = time.value.ifBlank { null },
                radius = if(today.value)
                    meetFilters.value.radius else null,
                lat = if(today.value)
                    meetFilters.value.lat else null,
                lng = if(today.value)
                    meetFilters.value.lng else null
            )
        )
        _meetings.emit(meetManager.getMeetings(meetFilters.value))
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun resetMeets() {
        meetManager.resetMeets()
        getMeets()
    }
    
    suspend fun meetInteraction(
        direction: DirectionType,
        meet: MeetingModel,
        state: SwipeableCardState,
    ) {
        state.swipe(direction)
        _meetings.emit(meetings.value - meet)
        if(direction == LEFT)
            meetManager.notInteresting(meet.id)
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
        _meetFilters.emit(
            MeetFiltersModel(get(today.value.compareTo(false)))
        )
        getMeets()
    }
    
    suspend fun navBarNavigate(point: Int): String {
        val list = arrayListOf<NavIconState>()
        repeat(navBar.value.size) {
            list.add(
                when {
                    navBar.value[it] == NEW_INACTIVE -> NEW_INACTIVE
                    it == point -> ACTIVE
                    else -> INACTIVE
                }
            )
        }
        navBarSetStates(list)
        return when(point) {
            1 -> "notification/list"
            2 -> {
                meetManager.clearAddMeet()
                "addmeet/category"
            }
            3 -> "chats/main"
            4 -> "profile/main"
            else -> "main/meetings"
        }
    }
    
    suspend fun changeGroup(today: Boolean) {
        _today.emit(today)
        getMeets()
    }
}