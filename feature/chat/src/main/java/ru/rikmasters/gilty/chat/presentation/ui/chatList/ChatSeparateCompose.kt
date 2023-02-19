package ru.rikmasters.gilty.chat.presentation.ui.chatList

import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek.Companion.displayRodName
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.COMPLETED
import java.util.Locale

fun getSortedChats(chats: List<ChatModel>) = Pair(
    chats.filter { it.meetStatus != COMPLETED }
        .sortedBy { it.datetime }
        .groupBy { it.datetime }
        .map { chat ->
            Pair(
                when {
                    todayControl(chat.key) -> "Сегодня"
                    tomorrowControl(chat.key) -> "Завтра"
                    else -> {
                        val it = LocalDate.of(chat.key)
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
                    }
                }, chat.value
            )
        },
    chats.filter { it.meetStatus == COMPLETED }
)