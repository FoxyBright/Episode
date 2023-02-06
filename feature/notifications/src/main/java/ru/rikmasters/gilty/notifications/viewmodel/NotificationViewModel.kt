package ru.rikmasters.gilty.notifications.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel.NotificationGroup.EARLY
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel.NotificationGroup.TODAY
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel.NotificationGroup.WEEK
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.common.extentions.weekControl
import ru.rikmasters.gilty.shared.model.notification.*

class NotificationViewModel: ViewModel() {
    
    private val _notifications = MutableStateFlow(emptyList<NotificationModel>())
    val notifications = _notifications.asStateFlow()
    
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
    
    enum class NotificationGroup { TODAY, WEEK, EARLY }
    
    fun getNotificationBy(group: NotificationGroup) =
        notifications.value.getNotificationBy(group)
    
    private fun List<NotificationModel>.getNotificationBy(
        group: NotificationGroup,
    ) = this.filter {
        when(group) {
            TODAY -> todayControl(it.date)
            WEEK -> weekControl(it.date) && !todayControl(it.date)
            EARLY -> !weekControl(it.date) && !todayControl(it.date)
        }
    }
}