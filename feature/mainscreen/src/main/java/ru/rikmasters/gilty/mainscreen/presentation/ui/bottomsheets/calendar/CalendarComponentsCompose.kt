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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar.CalendarMonth.Companion.weekDays
import ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar.DayType.*
import ru.rikmasters.gilty.shared.common.extentions.LocalDate
import ru.rikmasters.gilty.shared.common.extentions.toSp

@Composable
fun Month(
    date: LocalDate,
    selected: List<String>,
    onItemSelect: (String) -> Unit,
) {
    Column(
        Modifier.padding(horizontal = 8.dp),
        SpaceBetween, CenterHorizontally
    ) {
        ItemRow(weekDays) { Day(it.day, WEEKDAY) }
        remember { daysCompositor(date) }.forEach { row ->
            ItemRow(row) {
                val dateString = it.map()
                val inactive = LocalDate
                    .of(dateString)
                    .isBefore(LocalDate.now())
                Day(
                    label = it.day,
                    type = when {
                        inactive -> INACTIVE
                        selected.contains(dateString) -> CHECKED
                        else -> NORMAL
                    }
                ) { onItemSelect(dateString) }
            }
        }
    }
}

@Composable
private fun ItemRow(
    list: List<CalendarMonth>,
    item: @Composable (CalendarMonth) -> Unit,
) = Row(
    Modifier.fillMaxWidth(),
    SpaceBetween, CenterVertically
) {
    list.forEach { cd ->
        cd.day?.let { item(cd) }
            ?: Day((""), INACTIVE)
    }
}

private enum class DayType { INACTIVE, NORMAL, CHECKED, WEEKDAY }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Day(
    label: String?,
    type: DayType,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        onClick = { onClick?.let { it() } },
        modifier = modifier,
        enabled = (type != INACTIVE),
        shape = CircleShape,
        colors = cardColors(
            containerColor = if(type == CHECKED)
                colorScheme.primary else Transparent,
            disabledContainerColor = Transparent
        )
    ) {
        Box(
            Modifier.size(32.dp),
            Alignment.Center
        ) {
            Text(
                text = label ?: "",
                modifier = Modifier,
                color = when(type) {
                    INACTIVE, WEEKDAY -> colorScheme.onTertiary
                    NORMAL -> colorScheme.tertiary
                    CHECKED -> White
                },
                style = typography
                    .bodyMedium.copy(
                        fontSize = if(type == WEEKDAY)
                            13.dp.toSp() else 16.dp.toSp(),
                        fontWeight = SemiBold,
                        textAlign = Center
                    )
            )
        }
    }
}