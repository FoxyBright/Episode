package ru.rikmasters.gilty.mainscreen.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity.MODE_PRIVATE
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.app.checkGPS
import ru.rikmasters.gilty.core.app.checkLocationPermissions
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard.SwipeableCardState
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.MeetFilterGroupType.Companion.get
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW_INACTIVE
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.MeetFiltersModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

class MainViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    private val chatManager by inject<ChatManager>()
    private val context = getKoin().get<Context>()
    
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
    
    private val _meetFilters = MutableStateFlow(MeetFiltersModel())
    val meetFilters = _meetFilters.asStateFlow()
    
    private val _unreadMessages = MutableStateFlow(
        lazy {
            val count = context.getSharedPreferences(
                "sharedPref", MODE_PRIVATE
            ).getInt("unread_messages", 0)
            if(count > 0) NEW_INACTIVE else INACTIVE
        }.value
    )
    val unreadMessages = _unreadMessages.asStateFlow()
    
    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(
            if(hasUnread) NEW_INACTIVE else INACTIVE
        )
    }
    
    private val _unreadNotifications =
        MutableStateFlow(
            lazy {
                val count = context.getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).getInt("unread_notification", 0)
                if(count > 0) NEW_INACTIVE else INACTIVE
            }.value
        )
    val unreadNotifications =
        _unreadNotifications.asStateFlow()
    
    suspend fun setUnreadNotifications(hasUnread: Boolean) {
        _unreadNotifications.emit(
            if(hasUnread) NEW_INACTIVE else INACTIVE
        )
    }
    
    suspend fun getUnread() {
        chatManager.updateUnreadMessages().on(
            success = {
                context.getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).edit()
                    .putInt(
                        "unread_messages",
                        it.unreadCount
                    )
                    .putInt(
                        "unread_notification",
                        it.notificationsUnread
                    )
                    .apply()
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun getAllCategories() {
        meetManager.getCategoriesList().on(
            success = { _categories.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun getUserCategories() {
        profileManager.getUserCategories().on(
            success = { _userCategories.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
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
    
    val location =
        MutableStateFlow<Pair<Double, Double>?>(null)
    
    @SuppressLint("MissingPermission")
    suspend fun getLocation(activity: Activity) {
        if(!activity.checkLocationPermissions()
            || !activity.checkGPS()
        ) return
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener {
                coroutineScope.launch {
                    location.emit(
                        it?.let {
                            it.latitude to it.longitude
                        }
                    )
                }
            }
    }
    
    suspend fun getMeets() = singleLoading {
        _meetFilters.emit(
            meetFilters.value.copy(
                group = get(today.value.compareTo(false)),
                dates = days.value.ifEmpty { null },
                time = time.value.ifBlank { null },
                radius = if(
                    today.value
                    && location.value?.first != null
                    && location.value?.second != null
                ) meetFilters.value.radius else null,
                lat = if(today.value) location.value?.first else null,
                lng = if(today.value) location.value?.second else null
            )
        )
        meetManager.getMeetings(meetFilters.value).on(
            success = { _meetings.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun resetMeets() {
        meetManager.resetMeets().on(
            success = { getMeets() },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun meetInteraction(
        direction: DirectionType,
        meet: MeetingModel,
        state: SwipeableCardState,
    ) {
        state.swipe(direction)
        _meetings.emit(meetings.value - meet)
        if(direction == LEFT)
            meetManager.notInteresting(meet.id).on(
                success = {},
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
    }
    
    suspend fun changeGrid() {
        _grid.emit(!grid.value)
    }
    
    val filterCleaner = MutableStateFlow(false)
    
    suspend fun moreMeet() {
        _days.emit(emptyList())
        _time.emit("")
        _meetFilters.emit(
            MeetFiltersModel(
                get(today.value.compareTo(false))
            )
        )
        getMeets()
    }
    
    suspend fun navBarNavigate(point: Int) = when(point) {
        1 -> "notification/list"
        2 -> {
            meetManager.clearAddMeet()
            "addmeet/category"
        }
        3 -> "chats/main"
        4 -> "profile/main"
        else -> "main/meetings"
    }
    
    suspend fun changeGroup(today: Boolean) {
        _today.emit(today)
        getMeets()
    }
}