package ru.rikmasters.gilty.chat.presentation.ui.chatList

import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek.Companion.displayRodName
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import java.util.Locale

fun getSortedChats(
    chats: List<ChatModel>
): Pair<List<Pair<String, List<ChatModel>>>, List<ChatModel>> {
    
    val sortChats = chats.sortedBy {
        LocalDate.of(it.dateTime).millis()
    }
    
    val lastChats =
        arrayListOf<ChatModel>()
    val list =
        arrayListOf<Pair<String, List<ChatModel>>>()
    
    val dateList =
        arrayListOf<String>()
    sortChats.forEach {
        
        val date = it.dateTime
            .format(DATE_FORMAT)
        if(!dateList.contains(date) &&
            (LocalDate.of(date).isAfter(LOCAL_DATE)
                    || todayControl(date))
        )
            dateList.add(date)
        if(LocalDate.of(date)
                .isBefore(LOCAL_DATE)
        ) lastChats.add(it)
    }
    
    dateList.forEach { date ->
        val chatList =
            arrayListOf<ChatModel>()
        sortChats.forEach { chat ->
            val meetDate =
                LocalDate.of(chat.dateTime)
            
            if(meetDate.toString() == date)
                chatList.add(chat)
        }
        
        list.add(
            Pair(
                if(todayControl(date)) "Сегодня" else {
                    val it = LocalDate.of(date)
                    val dayOfWeek = it.dayOfWeek()
                    "${
                        if(dayOfWeek.ordinal == 2)
                            "Во" else "В"
                    } ${
                        dayOfWeek.displayRodName()
                            .lowercase(Locale.getDefault())
                    } ${it.day()} ${
                        Month.of(it.month())
                            .displayRodName()
                            .lowercase(Locale.getDefault())
                    }"
                },
                chatList
            )
        )
    }
    
    return Pair(list, lastChats)
}