package ru.rikmasters.gilty.notifications.presentation.ui.item

import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.common.extentions.weekControl
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationsByDateSeparator(private val notifications: List<NotificationModel>) {

    private enum class Groups { Today, Week, Early }

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
                    if (todayControl(it.date)
                    ) list.add(it)
                }
            }

            Groups.Week -> {
                notifications.forEach {
                    if (weekControl(it.date) &&
                        !todayControl(it.date)
                    ) list.add(it)
                }
            }

            Groups.Early -> {
                notifications.forEach {
                    if (!weekControl(it.date) &&
                        !todayControl(it.date)
                    ) list.add(it)
                }
            }
        }
        return list
    }
}