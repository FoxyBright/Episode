package ru.rikmasters.gilty.presentation.ui.presentation.notification

import ru.rikmasters.gilty.presentation.model.profile.notification.NotificationModel
import ru.rikmasters.gilty.utility.extentions.format
import java.time.LocalDateTime

class NotificationsByDateSeparator(private val notifications: List<NotificationModel>) {

    private enum class Groups { Today, Week, Early }

    private val todayDate = LocalDateTime.now().format("yyyy-MM-dd")

    fun getTodayList(): List<NotificationModel> {
        return cycle(Groups.Today)
    }

    fun getWeekList(): List<NotificationModel> {
        return cycle(Groups.Week)
    }

    fun getEarlyList(): List<NotificationModel> {
        return cycle(Groups.Early)
    }

    private fun cycle(group: Groups): List<NotificationModel> {
        val list = arrayListOf<NotificationModel>()
        when (group) {
            Groups.Today -> {
                notifications.forEach {
                    if (it.date.format("yyyy-MM-dd") == todayDate) list.add(it)
                }
            }

            Groups.Week -> {
                notifications.forEach {
                    if (it.date.format("yyyy-MM-dd") != todayDate) list.add(it)
                }
            }

            Groups.Early -> {}
        }
        return list
    }
}