package ru.rikmasters.gilty.notifications.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.push.NotificationManager
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel

class NotificationViewModel: ViewModel(), PullToRefreshTrait {
    
    private val notificationManger by inject<NotificationManager>()
    private val meetingManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _notifications = MutableStateFlow(emptyList<NotificationModel>())
    val notifications = _notifications.asStateFlow()
    
    private val navBarStateList = listOf(
        INACTIVE, ACTIVE, INACTIVE, INACTIVE, INACTIVE
    )
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private val _lastRespond = MutableStateFlow(Pair(0, ""))
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _blur = MutableStateFlow(false)
    val blur = _blur.asStateFlow()
    
    override suspend fun forceRefresh() = singleLoading {
        _notifications.emit(notificationManger.getNotification(1))
    }
    
    private var page = 1
    suspend fun getNotification() = singleLoading {
        val list =
            notificationManger.getNotification(page)
        _notifications.emit(notifications.value + list)
        page++
    }
    
    suspend fun getLastResponse() = singleLoading {
        val user = profileManager.getProfile(false)
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
    
    suspend fun selectNotification(
        notification: NotificationModel,
    ) = singleLoading {
        notification.parent.meeting?.let { meet ->
            getParticipants(meet.id)
            _selectedNotification.emit(notification)
        }
    }
    
    private suspend fun navBarSetStates(
        states: List<NavIconState>,
    ) {
        _navBar.emit(states)
    }
    
    suspend fun clearSelectedNotification() {
        _selectedNotification.emit(null)
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
    
    suspend fun swipeNotification(
        notification: NotificationModel,
    ) = singleLoading {
        notificationManger.deleteNotifications(
            listOf(notification.id)
        )
        _notifications.emit(
            notifications.value - notification
        )
    }
    
    //TODO OBSERVABLE NOTIFICATION
    
    private val _ratings = MutableStateFlow(emptyList<RatingModel>())
    val ratings = _ratings.asStateFlow()
    
    private val _selectedNotification = MutableStateFlow<NotificationModel?>(null)
    val selectedNotification = _selectedNotification.asStateFlow()
    
    private val _participants = MutableStateFlow(emptyList<UserModel>())
    val participants = _participants.asStateFlow()
    
    private val _participantsStates = MutableStateFlow(emptyList<Int>())
    val participantsStates = _participantsStates.asStateFlow()
    
    suspend fun getRatings() = singleLoading {
        _ratings.emit(notificationManger.getRatings())
    }
    
    private suspend fun getParticipants(meetId: String) {
        _participants.emit(
            meetingManager.getMeetMembers(meetId, (true))
        )
    }
    
    suspend fun emojiClick(
        emoji: EmojiModel, meetId: String, userId: String,
    ) = singleLoading {
        makeToast("emoji: $emoji, meetId: $meetId, userId: $userId")
        notificationManger.putRatings(meetId, userId, emoji)
        getParticipants(meetId)
    }
    
    suspend fun selectParticipants(participant: Int) {
        val list = participantsStates.value
        _participantsStates.emit(
            if(list.contains(participant))
                list - participant
            else list + participant
        )
    }
}