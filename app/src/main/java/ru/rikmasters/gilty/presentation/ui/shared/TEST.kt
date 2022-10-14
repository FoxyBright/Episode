package ru.rikmasters.gilty.presentation.ui.shared

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun GiltyCalendar() {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyRow(
        Modifier.fillMaxSize(),
        listState
    ) {
        items(12) {
            item(2022, it + 1)
            if (!listState.isScrollInProgress) {
                if (listState.firstVisibleItemScrollOffset <= 500)
                    coroutineScope.scrollBasic(listState, left = true)
                else coroutineScope.scrollBasic(listState)
                if (listState.firstVisibleItemScrollOffset > 500)
                    coroutineScope.scrollBasic(listState)
                else coroutineScope.scrollBasic(listState, left = true)
            }
        }
    }
}

private fun CoroutineScope.scrollBasic(listState: LazyListState, left: Boolean = false) {
    launch {
        val pos = if (left) listState.firstVisibleItemIndex else listState.firstVisibleItemIndex + 1
        listState.animateScrollToItem(pos)
    }
}

@Composable
private fun item(year: Int, month: Int) {
    val disMetrics = Resources.getSystem().displayMetrics
    Box(
        Modifier
            .width((disMetrics.widthPixels / disMetrics.density).dp)
            .height((disMetrics.heightPixels / disMetrics.density).dp)
            .padding(5.dp)
            .clip(CircleShape)
            .background(ThemeExtra.colors.cardBackground),
        Alignment.Center
    ) {
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            2 -> {
                if (year % 4 == 0) {
                    if (year % 100 == 0) {
                        if (year % 400 == 0) 29 else 28
                    } else 29
                } else 28
            }

            else -> 30
        }
        val dayOfWeek = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
        val list = arrayListOf<String>()
        Row(Modifier.padding(10.dp)) {
            dayOfWeek.forEach { dayOfWeekName ->
                list.clear()
                repeat(daysInMonth) {
                    val day = it + 1
                    if (dayOfWeekName.dayOfMonth(
                            LocalDate.of(year, month, day)
                        ).toString() == "$day"
                    ) list.add("$day")
                }
                Column(Modifier.padding(10.dp)) { list.forEach { Text(it) } }
            }
        }
    }
}

private fun DayOfWeek.dayOfMonth(dateInWeek: LocalDate = LocalDate.now()): Int {
    val firstDateOfWeek = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val dateOfDayOfWeek = firstDateOfWeek.with(TemporalAdjusters.nextOrSame(this))
    return dateOfDayOfWeek.dayOfMonth
}