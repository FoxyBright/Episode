package ru.rikmasters.gilty.shared.model.chat

enum class SortTypeModel {
    MEETING_DATE,
    MESSAGE_DATE,
}

fun SortTypeModel.getSortName():String = when(this) {
        SortTypeModel.MEETING_DATE -> "  По дате"
        SortTypeModel.MESSAGE_DATE -> "  По сообщению"
}