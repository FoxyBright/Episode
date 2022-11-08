package ru.rikmasters.gilty.notifications.presentation.ui

import ru.rikmasters.gilty.shared.common.extentions.FORMAT
import ru.rikmasters.gilty.shared.common.extentions.FULL_FORMAT
import ru.rikmasters.gilty.shared.common.extentions.toEpochMillis
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

//  TODO Проблема с часовым поясом, часы при фоматировании даты дает на 3 часа больше

fun getDifferenceOfTime(date: String): String {
    val now = LocalDate.now()
    val dateList = date.format(FULL_FORMAT).split("-")
    val localDate = LocalDate.of(
        dateList[0].toInt(),
        dateList[1].toInt(),
        dateList[2].toInt()
    )
    val c = Calendar.getInstance()
    val localHour = c.get(Calendar.HOUR_OF_DAY)
    val localMinute = c.get(Calendar.MINUTE)
    val localSecond = c.get(Calendar.SECOND)
    val dateHour = dateList[3].toInt()
    val dateMinute = dateList[4].toInt()
    val dateSecond = dateList[5].toInt()
    return if (todayControl(date))
        if (localHour - dateHour >= 1) "${localHour - dateHour} ч"
        else if (localMinute - dateMinute >= 1) "${localMinute - dateMinute} м"
        else "${localSecond - dateSecond} с"
    else "${now.dayOfYear - localDate.dayOfYear} д"

}

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
                    if (todayControl(it.date)) list.add(it)
                }
            }

            Groups.Week -> {
                notifications.forEach {
                    if (weekControl(it.date) && !todayControl(it.date)) list.add(it)
                }
            }

            Groups.Early -> {
                notifications.forEach {
                    if (!weekControl(it.date) && !todayControl(it.date)) list.add(it)
                }
            }
        }
        return list
    }

    private fun weekControl(date: String): Boolean {
        val dateList = date.format(FORMAT).split("-")
        val localDate = LocalDate.of(
            dateList.first().toInt(), dateList[1].toInt(), dateList.last().toInt()
        ).toEpochMillis() / 1000
        return (localDate in thisWeek().first..thisWeek().second)
    }

    private fun thisWeek(): Pair<Long, Long> {
        val format = DateTimeFormatter.ofPattern("dd")
        val now = LocalDate.now()
        val start = LocalDate.of(
            now.year,
            now.month,
            format.format(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
                .toInt()
        )
        return Pair(
            start.toEpochMillis() / 1000, LocalDate.of(
                now.year, now.month, format.format(start.plusDays(7)).toInt()
            ).toEpochMillis() / 1000
        )
    }
}