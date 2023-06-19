package ru.rikmasters.gilty.notifications.viewmodel

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.compose.runtime.*
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.notification.NotificationManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.*
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.notification.FeedBackModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel

class NotificationViewModel : ViewModel(), PullToRefreshTrait {

    private val notificationManger by inject<NotificationManager>()
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    private val chatManager by inject<ChatManager>()

    private val context = getKoin().get<Context>()

    private val _lastRespond =
        MutableStateFlow(LastRespond("", false, UserGroupTypeModel.DEFAULT, 0))
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
                "sharedPref", MODE_PRIVATE
            ).getInt("unread_messages", 0)
            if (count > 0) NEW_INACTIVE else INACTIVE
        }.value
    )
    val unreadMessages = _unreadMessages.asStateFlow()

    private val _unreadNotifications =
        MutableStateFlow(
            lazy {
                val count = context.getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).getInt("unread_notification", 0)
                if (count > 0) NEW_ACTIVE else ACTIVE
            }.value
        )
    val unreadNotifications =
        _unreadNotifications.asStateFlow()

    suspend fun setUnreadNotifications(hasUnread: Boolean) {
        _unreadNotifications.emit(
            if (hasUnread) NEW_INACTIVE else INACTIVE
        )
    }

    private val _ratings =
        MutableStateFlow(emptyList<RatingModel>())
    val ratings = _ratings.asStateFlow()

    private val _selectedNotification =
        MutableStateFlow<NotificationModel?>(null)
    val selectedNotification =
        _selectedNotification.asStateFlow()

    private val _meetId =
        MutableStateFlow<String?>(null)

    private val refreshMember =
        MutableStateFlow(false)

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

    private val _participantsStates =
        MutableStateFlow(emptyList<Int>())
    val participantsStates =
        _participantsStates.asStateFlow()

    // private var currentIndexToSplit = 0

    val splitMonthNotifications =
        MutableStateFlow(emptyList<Triple<Int, Boolean, NotificationModel>>())

    suspend fun getUnread() {
        notificationManger.markNotifyAsRead(
            readAll = true
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
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

    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(if (hasUnread) NEW_INACTIVE else INACTIVE)
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
            forceRefreshMembers()
            _selectedNotification.emit(notification)
        }
    }

    suspend fun clearSelectedNotification() {
        _selectedNotification.emit(null)
    }

    suspend fun navBarNavigate(
        point: Int,
    ) = when (point) {
        0 -> "main/meetings"
        2 -> {
            meetManager.clearAddMeet()
            "addmeet/category"
        }

        3 -> "chats/main"
        4 -> "profile/main"
        else -> "notification/list"
    }

    suspend fun swipeToDeleteNotification(
        notification: NotificationModel,
    ) = singleLoading {
        notificationManger.deleteNotifications(
            listOf(notification.id)
        ).on(
            success = {
                // deletes item locally after success
                deleteLocalNotifications(notification)
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )

    }

    suspend fun getRatings() = singleLoading {
        notificationManger.getRatings().on(
            success = { _ratings.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    private fun changeMeetId(meetId: String) {
        _meetId.value = meetId
    }

    suspend fun emojiClick(
        emoji: EmojiModel,
        meetId: String,
        userId: String,
        isOrganizer: Boolean,
    ) = singleLoading {

        notificationManger.putRatings(meetId, userId, emoji).on(
            success = {
                if (it) {
                    val feedback =
                        _selectedNotification.value
                            ?.feedback?.ratings?.toMutableList()
                            ?: mutableListOf()
                    feedback.add(RatingModel("0.0", emoji))
                    _selectedNotification.emit(
                        _selectedNotification
                            .value?.copy(
                                feedback = _selectedNotification
                                    .value?.feedback?.copy(
                                        ratings = feedback
                                    ) ?: FeedBackModel(
                                    ratings = feedback.toList(),
                                    respond = null
                                )
                            )
                    )

                    // Updates list item after putting rating
                    updateLocalNotifications(
                        _selectedNotification.value
                    )
                }
                changeMeetId(meetId)
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    private suspend fun updateLocalNotifications(notification: NotificationModel?) {
        val splitByMonthList = splitMonthNotifications.value.toMutableList()
        val indexOfSelected =
            splitByMonthList.indexOfFirst { item -> item.third.id == notification?.id }
        if (indexOfSelected != -1) {
            notification?.let { item ->
                splitByMonthList[indexOfSelected] =
                    splitByMonthList[indexOfSelected].copy(third = item)
            }
            splitMonthNotifications.emit(splitByMonthList)
        }
    }

    private suspend fun deleteLocalNotifications(notification: NotificationModel?) {

        val splitByMonthList = splitMonthNotifications.value.toMutableList()
        val indexOfSelected =
            splitByMonthList.indexOfFirst { item -> item.third.id == notification?.id }
        if (indexOfSelected != -1) {
            splitByMonthList[indexOfSelected] =
                splitByMonthList[indexOfSelected].copy(second = true)
            //splitByMonthList.removeAt(indexOfSelected)
            splitMonthNotifications.emit(splitByMonthList)
        }

    }

    suspend fun selectParticipants(participant: Int) {
        val list = participantsStates.value
        _participantsStates.emit(
            if (list.contains(participant)) {
                list - participant
            } else list + participant
        )
    }

    suspend fun forceRefreshMembers() {
        refreshMember.emit(!refreshMember.value)
    }

    suspend fun splitByMonthSM(notifications: List<NotificationModel>) {
        Log.d("Hello", "Here ${notifications.size}")
        // 20 - today
        // 30 - yesterday
        // 40 - this week
        // 50 - month earlier
        // 1-12 - months
        var currentIndexToSplit =
            splitMonthNotifications.value.size  // if(splitMonthNotifications.value.isEmpty()) 0 else splitMonthNotifications.value.size - 1

        if (notifications.size < currentIndexToSplit) {
            currentIndexToSplit = 0
            splitMonthNotifications.emit(emptyList())
        }
        val list = splitMonthNotifications.value.toMutableList()
        for (current in currentIndexToSplit until notifications.size) {
            val curDate = notifications[current].date
            if (todayControl(curDate)) {
                list.add(Triple(20, false, notifications[current]))
            } else if (yesterdayControl(curDate)) {
                list.add(Triple(30, false, notifications[current]))
            } else if (weekControl(curDate)) {
                list.add(Triple(40, false, notifications[current]))
            } else if (monthControl(curDate)) {
                list.add(Triple(50, false, notifications[current]))
            } else {
                list.add(Triple(getMonth(curDate), false, notifications[current]))
            }
        }
        //currentIndexToSplit = notifications.size
        splitMonthNotifications.emit(list)
    }
}
