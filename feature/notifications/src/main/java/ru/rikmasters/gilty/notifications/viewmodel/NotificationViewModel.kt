package ru.rikmasters.gilty.notifications.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.common.extentions.weekControl
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.notification.*

class NotificationViewModel: ViewModel() {
    
    private val _notifications = MutableStateFlow(emptyList<NotificationModel>())
    val notifications = _notifications.asStateFlow()
    
    private val _blur = MutableStateFlow(false)
    val blur = _blur.asStateFlow()
    
    private val _selectedNotify = MutableStateFlow<NotificationModel?>(null)
    val selectedNotify = _selectedNotify.asStateFlow()
    
    suspend fun getNotification() {
        val list = listOf(
            DemoNotificationLeaveEmotionModel,
            DemoNotificationMeetingOverModel,
            DemoTodayNotificationRespondAccept,
            DemoNotificationMeetingOverModel,
            DemoTodayNotificationRespondAccept,
            DemoTodayNotificationRespondAccept,
            DemoNotificationMeetingOverModel,
        )
        _notifications.emit(list)
    }
    
    suspend fun blur(state: Boolean) {
        _blur.emit(state)
    }
    
    suspend fun selectNotification(notify: NotificationModel) {
        _selectedNotify.emit(notify)
    }
    
    fun sortNotification(list: List<NotificationModel>) = Triple(
        list.filter { todayControl(it.date) },
        list.filter { weekControl(it.date) && !todayControl(it.date) },
        list.filter { !weekControl(it.date) && !todayControl(it.date) },
    )
    
    private val navBarStateList = listOf(
        INACTIVE, ACTIVE, INACTIVE, INACTIVE, INACTIVE
    )
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private suspend fun navBarSetStates(
        states: List<NavIconState>,
    ) {
        _navBar.emit(states)
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
            0 -> "main/meetings"
            2 -> "addmeet/category"
            3 -> "chats/main"
            4 -> "profile/main"
            else -> "notification/list"
        }
    }
    
    suspend fun emojiClick(
        emoji: EmojiModel,
        notify: NotificationModel,
    ) {
        makeToast("Emoji: $emoji, notify: $notify")
    }
    
    suspend fun swipeNotification(notify: NotificationModel) {
        makeToast("тут запрос на удаление уведомления")
    }
}

