package ru.rikmasters.gilty.chats.models.chat

import ru.rikmasters.gilty.shared.model.chat.SortTypeModel

enum class SortType(val stringName: String) {
    MEETING_DATE("meeting_date"),
    MESSAGE_DATE("message_date"),
    NONE("none"),
    MESSAGE_COUNT("message_count");

    fun map() = when (this) {
        MEETING_DATE -> SortTypeModel.MEETING_DATE
        MESSAGE_DATE -> SortTypeModel.MESSAGE_DATE
        MESSAGE_COUNT -> SortTypeModel.MESSAGE_COUNT
        NONE -> SortTypeModel.NONE
    }
}

fun SortTypeModel.mapDTO() = when (this) {
    SortTypeModel.MEETING_DATE -> SortType.MEETING_DATE
    SortTypeModel.MESSAGE_DATE -> SortType.MESSAGE_DATE
    SortTypeModel.MESSAGE_COUNT -> SortType.MESSAGE_COUNT
    SortTypeModel.NONE -> SortType.NONE
}
