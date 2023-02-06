package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.calendar.DayType.CHECKED
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.calendar.DayType.INACTIVE
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.calendar.DayType.NORMAL
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek
import ru.rikmasters.gilty.shared.common.extentions.DayOfWeek.Companion.displayShort
import ru.rikmasters.gilty.shared.common.extentions.LOCAL_DATE
import ru.rikmasters.gilty.shared.common.extentions.LocalDate
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.METRICS
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun CalendarBsPreview() {
    GiltyTheme {
        val year = LOCAL_DATE.year()
        val month = LOCAL_DATE.month()
        val day = LOCAL_DATE.day()
        val strMonth = "${
            if(month in 1..9) "0$month" else month
        }"
        val list = remember {
            mutableStateListOf(
                "$year-$strMonth-${
                    if(day + 1 in 1..9)
                        "0${day + 1}" else day + 1
                }",
                "$year-$strMonth-${
                    if(day + 3 in 1..9)
                        "0${day + 3}" else day + 3
                }",
                "$year-$strMonth-${
                    if(day + 7 in 1..9)
                        "0${day + 7}" else day + 7
                }",
            )
        }
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            CalendarBsContent(
                CalendarBSState(list)
            )
        }
    }
}

data class CalendarBSState(
    val days: List<String>,
)

interface CalendarBSCallback {
    
    fun onItemSelect(date: String)
    fun onSave()
    fun onClear()
}

@Composable
fun CalendarBsContent(
    state: CalendarBSState,
    modifier: Modifier = Modifier,
    callback: CalendarBSCallback? = null,
) {
    val thisMounth = LOCAL_DATE.atStartOfMounth()
    val nextMounth = thisMounth.plusMonths(1)
    val listState = rememberLazyListState()
    val mounthState = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }.value
    Column(
        modifier.padding(top = 28.dp),
        SpaceBetween
    ) {
        Row(
            Modifier.fillMaxWidth(),
            SpaceBetween, CenterVertically
        ) {
            Text(
                if(mounthState == 0)
                    thisMounth.monthName()
                else nextMounth.monthName(),
                Modifier.padding(start = 16.dp), colorScheme.tertiary,
                style = typography.labelLarge,
            )
            if(state.days.isNotEmpty()) Text(
                stringResource(R.string.meeting_filter_clear),
                Modifier
                    .padding(end = 16.dp)
                    .clickable { callback?.onClear() },
                colorScheme.primary,
                style = typography.bodyMedium,
                fontWeight = Medium
            )
        }
        val scope = rememberCoroutineScope()
        val offset = remember {
            derivedStateOf {
                listState.firstVisibleItemScrollOffset
            }
        }.value
        if(!listState.isScrollInProgress) {
            if(offset <= 250) scope.scrollBasic(listState, (true))
            else scope.scrollBasic(listState)
            if(offset > 250) scope.scrollBasic(listState)
            else scope.scrollBasic(listState, (true))
        }
        LazyRow(
            Modifier.padding(
                top = 18.dp,
                start = 10.dp
            ), listState
        ) {
            item((0), (0)) {
                Mounth("$thisMounth", state.days)
                { callback?.onItemSelect(it) }
            }
            item { Spacer(modifier.width(16.dp)) }
            item((1), (0)) {
                Mounth("$nextMounth", state.days)
                { callback?.onItemSelect(it) }
            }
        }
        GradientButton(
            Modifier.padding(16.dp, 36.dp),
            stringResource(R.string.save_button), true
        ) { callback?.onSave() }
    }
}

private fun CoroutineScope.scrollBasic(
    listState: LazyListState,
    left: Boolean = false,
) {
    launch {
        listState.animateScrollToItem(
            if(left) listState.firstVisibleItemIndex
            else listState.firstVisibleItemIndex + 2
        )
    }
}

@Composable
private fun Mounth(
    date: String,
    dateList: List<String>,
    modifier: Modifier = Modifier,
    onDayClick: ((String) -> Unit)? = null,
) {
    val locDate = LocalDate.of(date)
    LazyVerticalGrid(
        Fixed(7),
        modifier
            .width((METRICS.widthPixels / METRICS.density - 22).dp)
            .wrapContentHeight(),
        verticalArrangement = spacedBy(12.dp),
        horizontalArrangement = spacedBy(10.dp),
    ) {
        items(DayOfWeek.list(), { it.name })
        { Day(it.displayShort().uppercase(), INACTIVE) }
        items(locDate.firstDayOfWeek().ordinal, { "$it" })
        { Day(stringResource(R.string.empty_String), INACTIVE) }
        items(locDate.lengthOfMounth(), { it })
        {
            val month = remember { locDate.month() }
            val day = remember { it + 1 }
            val thisDate = remember {
                "${locDate.year()}-${
                    if(month in 1..9) "0$month" else month
                }-${if(day - 1 in 1..9) "0$day" else day}"
            }
            Day(
                "$day", when {
                    dateList.contains(thisDate) -> CHECKED
                    LocalDate.of(thisDate)
                        .isBefore(LOCAL_DATE) -> INACTIVE
                    
                    else -> NORMAL
                }
            ) { onDayClick?.let { c -> c(thisDate) } }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Day(
    label: String,
    type: DayType,
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        Modifier
            .width(32.dp)
            .height(44.dp), (type != INACTIVE), CircleShape,
        cardColors(
            if(type == CHECKED)
                colorScheme.primary
            else Transparent,
            disabledContainerColor = Transparent
        )
    ) {
        Box(Modifier.fillMaxSize(), Center) {
            Text(
                label, Modifier, when(type) {
                    INACTIVE -> colorScheme.onTertiary
                    NORMAL -> colorScheme.tertiary
                    CHECKED -> White
                }, style = typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private enum class DayType {
    INACTIVE, NORMAL, CHECKED
}