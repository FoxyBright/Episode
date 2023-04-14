package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.string.add_meet_detailed_meet_place
import ru.rikmasters.gilty.shared.common.*
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun MeetingFilterBottomPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            MeetingFilterBottom(
                Modifier, (26), FilterListState(
                    (true), (false), (25), (false),
                    listOf(0, 2), emptyList(),
                    listOf("kaif", "pain", "fast", "launch")
                        .map { TagModel(it, it) },
                    listOf(), listOf(), listOf(),
                    listOf(1), DemoCityModel, true
                )
            )
        }
    }
}

data class FilterListState(
    val today: Boolean,
    val distanceState: Boolean,
    val distance: Int,
    val onlyOnline: Boolean,
    val meetType: List<Int>,
    val conditionList: List<Int>,
    val tags: List<TagModel>,
    val interest: List<CategoryModel>,
    val categories: List<CategoryModel>,
    val selectedCategories: List<CategoryModel>,
    val categoriesStates: List<Int>,
    val city: CityModel?,
    val hasFilters: Boolean,
)

interface MeetingFilterBottomCallback {
    
    fun onFilter()
    fun onCategoryClick(index: Int, category: CategoryModel)
    fun onSubClick(parent: CategoryModel)
    fun onAllCategoryClick()
    fun onFilterClick()
    fun onDeleteTag(tag: TagModel)
    fun onDistanceClick()
    fun onDistanceValueChange(it: Int)
    fun onOnlyOnlineClick()
    fun onMeetingTypeSelect(index: Int)
    fun onConditionSelect(index: Int)
    fun onCityClick()
    fun onClear()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetingFilterBottom(
    modifier: Modifier = Modifier,
    find: Int? = null,
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null,
) {
    Scaffold(
        modifier,
        topBar = {
            // TODO при свернутом BS
            /*TopBar(state, callback)*/
        },
        bottomBar = {
            Buttons(
                find, state.hasFilters,
                Modifier, { callback?.onFilter() }
            ) { callback?.onClear() }
        }
    ) { padding ->
        Content(
            state, callback, Modifier.padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() - 40.dp
            )
        )
    }
}

@Composable
@Suppress("unused")
private fun TopBar(
    state: FilterListState,
    callback: MeetingFilterBottomCallback?,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier
            .fillMaxWidth()
            .padding(
                top = 28.dp,
                bottom = 6.dp
            )
    ) {
        itemSpacer(16.dp, true)
        items(state.interest) {
            GChip(
                Modifier.padding(end = 8.dp), it.name,
                state.selectedCategories.contains(it)
            ) { callback?.onSubClick(it) }
        }
        itemSpacer(8.dp, true)
    }
}

@Composable
private fun Content(
    state: FilterListState,
    callback: MeetingFilterBottomCallback?,
    modifier: Modifier = Modifier,
) {
    val list = filterList(state, callback)
    LazyColumn(modifier.fillMaxSize()) {
        items(list) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    it.name, Modifier.padding(
                        top = if(
                            it.name == stringResource(R.string.meeting_filter_category)
                        ) 0.dp else 28.dp,
                        bottom = 18.dp
                    ), colorScheme.tertiary,
                    style = typography.labelLarge
                )
                it.content.invoke()
            }
        }
        itemSpacer(20.dp)
    }
}

@Composable
private fun Buttons(
    find: Int?,
    hasFilter: Boolean,
    modifier: Modifier,
    onFilter: () -> Unit,
    onClear: () -> Unit,
) {
    Column(modifier) {
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(
                    top = 28.dp,
                    bottom = if(hasFilter)
                        0.dp else 28.dp
                ), stringResource(R.string.confirm_button),
            smallText = find?.let {
                stringResource(
                    R.string.meeting_filter_meeting_find, it
                )
            }
        ) { onFilter() }
        if(hasFilter) Text(
            stringResource(R.string.meeting_filter_clear),
            Modifier
                .fillMaxWidth()
                .clickable { onClear() }
                .padding(top = 12.dp, bottom = 28.dp),
            colorScheme.tertiary,
            textAlign = Center,
            style = typography.bodyLarge
        )
    }
}

@Composable
private fun filterList(
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null,
): List<FilterModel> {
    val filters = arrayListOf(
        FilterModel(stringResource(R.string.meeting_filter_category)) {
            Category(
                state.categories,
                state.categoriesStates,
                state.selectedCategories,
                { index, category ->
                    callback?.onCategoryClick(index, category)
                }, { callback?.onSubClick(it) },
                { callback?.onAllCategoryClick() }
            )
        },
        FilterModel(stringResource(R.string.meeting_filter_tag_search)) {
            Tags(
                state.tags,
                { callback?.onFilterClick() },
            ) { callback?.onDeleteTag(it) }
        },
        FilterModel(stringResource(R.string.meeting_filter_meet_type)) {
            MeetingType(
                state.onlyOnline,
                state.meetType,
                stringResource(R.string.meeting_only_online_meetings_button),
                (false), { callback?.onOnlyOnlineClick() },
                { callback?.onMeetingTypeSelect(it) }
            )
        },
        FilterModel(stringResource(R.string.meeting_filter_conditions)) {
            Conditions(state.conditionList) {
                callback?.onConditionSelect(it)
            }
        }
    )
    if(state.today) filters.add(
        (2),
        FilterModel(stringResource(R.string.meeting_filter_distance)) {
            Distance(
                state.distance,
                state.distanceState,
                { callback?.onDistanceClick() },
                { callback?.onDistanceValueChange(it) }
            )
        })
    else filters.add(
        (0), FilterModel(stringResource(add_meet_detailed_meet_place)) {
            CardRow(
                stringResource(R.string.select_city),
                state.city?.name ?: "",
                Modifier, shapes.medium
            ) { callback?.onCityClick() }
        }
    )
    return filters
}