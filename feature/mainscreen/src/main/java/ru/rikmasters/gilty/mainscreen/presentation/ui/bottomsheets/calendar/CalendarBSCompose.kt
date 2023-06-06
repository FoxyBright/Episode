package ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.LOCAL_DATE
import ru.rikmasters.gilty.shared.common.extentions.LocalDate
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun CalendarBsPreview() {
    GiltyTheme {
        val list = LOCAL_DATE.let {
            "${
                if(it.month() in 1..9)
                    "0${it.month()}" else it.month()
            }".let { strMonth ->
                listOf(
                    "${it.year()} -$strMonth-${
                        if(it.day() + 1 in 1..9)
                            "0${it.day() + 1}" else it.day() + 1
                    }",
                    "${it.year()}-$strMonth-${
                        if(it.day() + 3 in 1..9)
                            "0${it.day() + 3}" else it.day() + 3
                    }",
                    "${it.year()}-$strMonth-${
                        if(it.day() + 7 in 1..9)
                            "0${it.day() + 7}" else it.day() + 7
                    }",
                )
            }
        }
        Box(Modifier.background(colorScheme.background))
        { CalendarBsContent(CalendarBSState(list)) }
    }
}

data class CalendarBSState(val days: List<String>)

interface CalendarBSCallback {
    
    fun onItemSelect(date: String)
    fun onSave()
    fun onClear()
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CalendarBsContent(
    state: CalendarBSState,
    modifier: Modifier = Modifier,
    callback: CalendarBSCallback? = null,
) {
    val thisMonth =
        LOCAL_DATE.atStartOfMounth()
    val nextMonth =
        thisMonth.plusMonths(1)
    val pagerState =
        rememberPagerState()
    Column(
        modifier = modifier
            .padding(top = 28.dp),
        verticalArrangement = SpaceBetween
    ) {
        TopBar(
            name = if(pagerState.currentPage == 0)
                thisMonth.monthName()
            else nextMonth.monthName(),
            clearState = state.days.isNotEmpty()
        ) { callback?.onClear() }
        Calendar(
            modifier = Modifier,
            state = pagerState,
            selected = state.days,
            thisMonth = thisMonth,
            nextMonth = nextMonth
        ) { callback?.onItemSelect(it) }
        GradientButton(
            modifier = Modifier
                .padding(bottom = 54.dp)
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.save_button)
        ) { callback?.onSave() }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Calendar(
    modifier: Modifier,
    state: PagerState,
    selected: List<String>,
    thisMonth: LocalDate,
    nextMonth: LocalDate,
    onItemSelect: (String) -> Unit,
) {
    HorizontalPager(
        pageCount = 2,
        modifier = modifier
            .fillMaxWidth(),
        state = state,
    ) { page ->
        Month(
            date = if(page == 0)
                thisMonth else nextMonth,
            selected = selected
        ) { onItemSelect(it) }
    }
}

@Composable
private fun TopBar(
    name: String,
    clearState: Boolean,
    onClear: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        SpaceBetween, CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(start = 16.dp),
            color = colorScheme.tertiary,
            style = typography.labelLarge,
        )
        if(clearState) Text(
            text = stringResource(R.string.meeting_filter_clear),
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) { onClear() },
            color = colorScheme.primary,
            style = typography.bodyMedium,
            fontWeight = SemiBold
        )
    }
}