package ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar

import androidx.compose.runtime.Stable
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek

@Stable
internal data class CalendarMonth(
    val year: String = "",
    val month: String = "",
    val day: String? = null,
) {
    
    companion object {
        
        val weekDays = DayOfWeek.valueList()
            .map { CalendarMonth(day = it) }
    }
    
    fun map() = "$year-$month-$day"
}