package ru.rikmasters.gilty.chat.presentation.ui.chatList

import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek.Companion.display
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.ChatModel

fun getSortedByTimeChats(chats: List<ChatModel>) = chats
    .groupBy { it.datetime.take(10) }
    .map {
        when {
            todayControl(it.key) -> "Сегодня"
            tomorrowControl(it.key) -> "Завтра"
            else -> getDateString(LocalDate.of(it.key))
        } to it.value
    }

private fun getDateString(date: LocalDate): String {
    val weekDay = date.dayOfWeek()
    
    /*val prefix = if(weekDay.ordinal == 2)
        "Во" else "В"*/
    
    val wdName = weekDay
        .display()

    val month = Month
        .of(date.month())
        .displayRodName()
        .lowercase()
    
    return "$wdName ${date.day()} $month"
}