package ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar.CalendarMonth.Companion.weekDays
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar.DayType.*
import ru.rikmasters.gilty.shared.common.extentions.LocalDate

@Composable
fun Month(
    date: LocalDate,
    selected: List<String>,
    onItemSelect: (String) -> Unit,
) {
    val days = remember { daysCompositor(date) }
    Column(Modifier.fillMaxWidth(), SpaceBetween, CenterHorizontally) {
        ItemRow(weekDays) { Day(it.day, WEEKDAY) }
        days.forEach { row ->
            ItemRow(row) {
                val dateString = it.map()
                Day(
                    it.day, if(selected.contains(dateString))
                        CHECKED else NORMAL
                ) { onItemSelect(dateString) }
            }
        }
    }
}

@Composable
private fun ItemRow(
    list: List<CalendarMonth>,
    item: @Composable (CalendarMonth) -> Unit,
) = Row(Modifier, SpaceBetween, Alignment.CenterVertically) {
    list.forEach { cd ->
        cd.day?.let { item(cd) }
            ?: Day((""), INACTIVE)
    }
}

private enum class DayType { INACTIVE, NORMAL, CHECKED, WEEKDAY }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Day(
    label: String?, type: DayType,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        modifier, (type != INACTIVE),
        CircleShape, cardColors(
            if(type == CHECKED)
                colorScheme.primary
            else Transparent,
            disabledContainerColor = Transparent
        )
    ) {
        Box(Modifier.size(32.dp), Alignment.Center) {
            Text(
                label ?: "", Modifier, when(type) {
                    INACTIVE, WEEKDAY -> colorScheme.onTertiary
                    NORMAL -> colorScheme.tertiary
                    CHECKED -> White
                }, style = typography.bodyMedium.copy(
                    fontSize = if(type == WEEKDAY)
                        13.sp else 16.sp,
                    fontWeight = SemiBold,
                    textAlign = Center
                )
            )
        }
    }
}