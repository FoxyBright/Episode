package ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar

import ru.rikmasters.gilty.shared.common.extentions.LocalDate

internal fun daysCompositor(date: LocalDate): List<List<CalendarMonth>> {
    val monthLength = date.lengthOfMounth()
    val firstWD = date.firstDayOfWeek().ordinal
    val month = date.month().let { if(it < 10) "0$it" else "$it" }
    val year = date.year().toString()
    val daysList =
        (IntArray(firstWD) { 0 } + IntArray(monthLength) { it + 1 })
            .let { it + IntArray(42 - (firstWD + monthLength)) { 0 } }
    return daysList.map {
        val day = if(it == 0) null else "$it"
        CalendarMonth(year, month, day)
    }.chunked(7)
}