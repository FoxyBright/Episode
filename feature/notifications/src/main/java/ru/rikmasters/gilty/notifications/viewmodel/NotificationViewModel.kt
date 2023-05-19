package ru.rikmasters.gilty.notifications.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.notification.NotificationManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW_INACTIVE
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel

class NotificationViewModel: ViewModel(), PullToRefreshTrait {
    
    private val notificationManger by inject<NotificationManager>()
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    
    private val _lastRespond = MutableStateFlow(Pair(0, ""))
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _blur = MutableStateFlow(false)
    val blur = _blur.asStateFlow()
    
    private val refresh = MutableStateFlow(false)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications by lazy {
        refresh.flatMapLatest {
            notificationManger.getNotifications()
        }.cachedIn(coroutineScope)
    }
    
    private val _unreadMessages = MutableStateFlow(
        lazy {
            val count = getKoin().get<Context>().getSharedPreferences(
                "sharedPref", ComponentActivity.MODE_PRIVATE
            ).getInt("unread_messages", 0)
            if(count > 0) NEW_INACTIVE else INACTIVE
        }.value
    )
    val unreadMessages = _unreadMessages.asStateFlow()
    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(if(hasUnread) NEW_INACTIVE else INACTIVE)
    }
    
    override suspend fun forceRefresh() = singleLoading {
        refresh.value = !refresh.value
    }
    
    suspend fun getLastResponse() = singleLoading {
        _lastRespond.emit(
            profileManager.getNotificationResponds()
        )
    }
    
    suspend fun blur(state: Boolean) {
        _blur.emit(state)
        _participantsStates.emit(emptyList())
    }
    
    suspend fun selectNotification(
        notification: NotificationModel,
    ) = singleLoading {
        notification.parent.meeting?.let { meet ->
            changeMeetId(meet.id)
            refreshMember.emit(!refreshMember.value)
            _selectedNotification.emit(notification)
        }
    }
    
    suspend fun clearSelectedNotification() {
        _selectedNotification.emit(null)
    }
    
    suspend fun navBarNavigate(
        point: Int,
    ) = when(point) {
        0 -> "main/meetings"
        2 -> {
            meetManager.clearAddMeet()
            "addmeet/category"
        }
        3 -> "chats/main"
        4 -> "profile/main"
        else -> "notification/list"
    }
    
    suspend fun swipeNotification(
        notification: NotificationModel,
    ) = singleLoading {
        notificationManger.deleteNotifications(
            listOf(notification.id)
        )
    }
    
    private val _ratings = MutableStateFlow(emptyList<RatingModel>())
    val ratings = _ratings.asStateFlow()
    
    private val _selectedNotification =
        MutableStateFlow<NotificationModel?>(null)
    val selectedNotification = _selectedNotification.asStateFlow()
    
    private val _meetId = MutableStateFlow<String?>(null)

    private val refreshMember = MutableStateFlow<Boolean>(false)
    @OptIn(ExperimentalCoroutinesApi::class)
    val participants by lazy {
        refreshMember.flatMapLatest {
            _meetId.value?.let {
                meetManager.getMeetMembers(
                    meetId = it,
                    excludeMe = true
                )
            } ?: flow { }
        }
    }
    
    private val _participantsStates = MutableStateFlow(emptyList<Int>())
    val participantsStates = _participantsStates.asStateFlow()
    
    suspend fun getRatings() = singleLoading {
        _ratings.emit(notificationManger.getRatings())
    }
    
    private fun changeMeetId(meetId: String) {
        _meetId.value = meetId
    }
    
    suspend fun emojiClick(
        emoji: EmojiModel,
        meetId: String,
        userId: String,
    ) = singleLoading {
        notificationManger.putRatings(meetId, userId, emoji)
        changeMeetId(meetId)
        refreshMember.emit(!refreshMember.value)
    }
    
    suspend fun selectParticipants(participant: Int) {
        val list = participantsStates.value
        _participantsStates.emit(
            if(list.contains(participant)) {
                list - participant
            } else list + participant
        )
    }
}
