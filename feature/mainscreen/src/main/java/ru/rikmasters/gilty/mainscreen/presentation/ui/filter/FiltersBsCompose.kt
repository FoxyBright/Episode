package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
                    DemoCategoryModelList, DemoCategoryModelList,
                    listOf(), listOf(1), DemoCityModel,
                    (true), (1f)
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
    val alpha: Float,
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
    val mod = Modifier
        .alpha(state.alpha)
        .let {
            if(state.alpha <= 0.05f)
                if(state.alpha in 0.05f..0.02f)
                    it.fillMaxHeight(state.alpha)
                else it.height(16.dp)
            else it
        }
    Scaffold(
        modifier = modifier,
        topBar = { TopBar(state, callback, mod) },
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
private fun TopBar(
    state: FilterListState,
    callback: MeetingFilterBottomCallback?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 8.dp)
    ) {
        Spacer(Modifier.width(16.dp))
        state.interest.reversed().forEach {
            GChip(
                modifier = Modifier
                    .padding(end = 8.dp),
                text = it.name,
                isSelected = state.selectedCategories
                    .contains(it)
            ) { callback?.onSubClick(it) }
        }
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun Content(
    state: FilterListState,
    callback: MeetingFilterBottomCallback?,
    modifier: Modifier = Modifier,
) {
    Box(
        Modifier
            .fillMaxSize()
            .alpha(state.alpha)
            .background(
                colorScheme.primaryContainer
            )
    )
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        filterList(state, callback).forEachIndexed { i, it ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = it.name,
                    modifier = Modifier.padding(
                        top = if(i == 0) 0.dp
                        else 28.dp,
                        bottom = 18.dp
                    ),
                    color = colorScheme.tertiary,
                    style = typography.labelLarge
                )
                it.content()
            }
        }
        Spacer(Modifier.height(40.dp))
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
            text = stringResource(R.string.meeting_filter_clear),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClear() }
                .padding(
                    top = 12.dp,
                    bottom = 28.dp
                ),
            color = colorScheme.tertiary,
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
                categories = state.categories,
                states = state.categoriesStates,
                selected = state.selectedCategories,
                onCategoryClick = { index, category ->
                    callback?.onCategoryClick(index, category)
                },
                onSubClick = { callback?.onSubClick(it) },
                onAllCategoryClick = { callback?.onAllCategoryClick() }
            )
        },
        FilterModel(stringResource(R.string.meeting_filter_tag_search)) {
            Tags(
                tagList = state.tags,
                onClick = { callback?.onFilterClick() },
            ) { callback?.onDeleteTag(it) }
        },
        FilterModel(stringResource(R.string.meeting_filter_meet_type)) {
            MeetingType(
                checkState = state.onlyOnline,
                selected = state.meetType,
                checkLabel = stringResource(
                    R.string.meeting_only_online_meetings_button
                ),
                online = false,
                onOnlyOnlineClick = {
                    callback?.onOnlyOnlineClick()
                },
                onMeetingTypeSelect = {
                    callback?.onMeetingTypeSelect(it)
                }
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
                distance = state.distance,
                state = state.distanceState,
                onClick = { callback?.onDistanceClick() },
                onValueChange = { callback?.onDistanceValueChange(it) }
            )
        })
    else filters.add(
        (0), FilterModel(stringResource(add_meet_detailed_meet_place)) {
            CardRow(
                label = stringResource(R.string.select_city),
                text = state.city?.name ?: "",
                modifier = Modifier.padding(),
                shape = shapes.medium
            ) { callback?.onCityClick() }
        }
    )
    return filters
}