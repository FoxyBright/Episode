package ru.rikmasters.gilty.notifications.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.push.notification.NotificationManager
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.common.extentions.weekControl
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationViewModel: ViewModel() {
    
    private val notificationManger by inject<NotificationManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _notifications = MutableStateFlow(emptyList<NotificationModel>())
    val notifications = _notifications.asStateFlow()
    
    private val _selectedNotify = MutableStateFlow<NotificationModel?>(null)
    val selectedNotify = _selectedNotify.asStateFlow()
    
    private val _lastRespond = MutableStateFlow(Pair(0, ""))
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _blur = MutableStateFlow(false)
    val blur = _blur.asStateFlow()
    
    suspend fun getNotification() = singleLoading {
        _notifications.emit(notificationManger.getNotification())
    }
    
    suspend fun getLastResponse() = singleLoading {
        val user = profileManager.getProfile()
        _lastRespond.emit(
            Pair(
                user.respondsCount ?: 0,
                user.respondsImage?.url ?: ""
            )
        )
    }
    
    suspend fun blur(state: Boolean) {
        _blur.emit(state)
    }
    
    suspend fun selectNotification(notification: NotificationModel) {
        _selectedNotify.emit(notification)
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
    ) = singleLoading {
        makeToast("Emoji: $emoji, notify: $notify")
    }
    
    suspend fun swipeNotification(
        notification: NotificationModel,
    ) = singleLoading {
        makeToast("тут запрос на удаление уведомления")
        _notifications.emit(notifications.value - notification)
    }
}

