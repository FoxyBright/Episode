package ru.rikmasters.gilty.chat.presentation.ui.chatList

import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek.Companion.displayRodName
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import java.util.Locale

fun getSortedChats(chats: List<ChatModel>) =
    chats.groupBy { it.datetime }
        .map { chat ->
            Pair(
                when {
                    todayControl(chat.key) -> "Сегодня"
                    tomorrowControl(chat.key) -> "Завтра"
                    else -> {
                        val it = LocalDate.of(chat.key)
                        val dayOfWeek = it.dayOfWeek()
                        "${
                        if (dayOfWeek.ordinal == 2) {
                            "Во" 
                        }else "В"
                        } ${
                        dayOfWeek.displayRodName()
                            .lowercase(Locale.getDefault())
                        } ${it.day()} ${
                        Month.of(it.month())
                            .displayRodName()
                            .lowercase(Locale.getDefault())
                        }"
                    }
                },
                chat.value
            )
        }

fun List<ChatModel>.getSorted() =
    this.groupBy { it.datetime }
        .map { chat ->
            Pair(
                when {
                    todayControl(chat.key) -> "Сегодня"
                    tomorrowControl(chat.key) -> "Завтра"
                    else -> {
                        val it = LocalDate.of(chat.key)
                        val dayOfWeek = it.dayOfWeek()
                        "${
                        if (dayOfWeek.ordinal == 2) {
                            "Во" 
                        }else "В"
                        } ${
                        dayOfWeek.displayRodName()
                            .lowercase(Locale.getDefault())
                        } ${it.day()} ${
                        Month.of(it.month())
                            .displayRodName()
                            .lowercase(Locale.getDefault())
                        }"
                    }
                },
                chat.value
            )
        }
