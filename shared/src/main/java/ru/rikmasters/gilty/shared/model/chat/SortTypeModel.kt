package ru.rikmasters.gilty.shared.model.chat

enum class SortTypeModel {
    MEETING_DATE,
    MESSAGE_DATE,
    MESSAGE_COUNT,
    NONE,
}

fun SortTypeModel.getSortName():String = when(this) {
        SortTypeModel.MEETING_DATE -> "По дате"
        SortTypeModel.MESSAGE_DATE -> "По сообщению"
        SortTypeModel.MESSAGE_COUNT -> "По сообщению"
        else -> "По сообщению"
    }