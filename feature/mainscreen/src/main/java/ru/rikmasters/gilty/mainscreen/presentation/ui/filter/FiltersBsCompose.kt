package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.string.add_meet_detailed_meet_place
import ru.rikmasters.gilty.shared.common.*
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
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
                    listOf(1), ("Россия"), ("Москва"),
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
    val country: String,
    val city: String,
)

interface MeetingFilterBottomCallback {
    
    fun onNext()
    fun onBack()
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
    fun onCountryClick()
    fun onCityClick()
    fun onClear()
}


@Composable
fun MeetingFilterBottom(
    modifier: Modifier = Modifier,
    find: Int? = null,
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null,
) {
    val list = filterList(state, callback)
    Box(modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 28.dp, bottom = 18.dp)
                ) {
                    items(state.interest) {
                        GiltyChip(
                            Modifier.padding(end = 8.dp), it.name,
                            state.selectedCategories.contains(it)
                        ) { callback?.onSubClick(it) }
                    }
                }
            }
            items(list) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        it.name,
                        Modifier.padding(top = 28.dp, bottom = 18.dp),
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.labelLarge
                    )
                    it.content.invoke()
                }
            }
            item {
                GradientButton(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 28.dp),
                    stringResource(R.string.confirm_button),
                    smallText = find?.let {
                        stringResource(R.string.meeting_filter_meeting_find, it)
                    }
                ) { callback?.onNext() }
            }
            item {
                Text(
                    stringResource(R.string.meeting_filter_clear),
                    Modifier
                        .fillMaxWidth()
                        .clickable { callback?.onClear() }
                        .padding(top = 12.dp, bottom = 28.dp),
                    colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
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
            TagSearch(
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
        (2), FilterModel(stringResource(R.string.meeting_filter_distance)) {
            Distance(
                state.distance,
                state.distanceState,
                { callback?.onDistanceClick() },
                { callback?.onDistanceValueChange(it) }
            )
        })
    else filters.add(
        (0), FilterModel(stringResource(add_meet_detailed_meet_place)) {
            Country(state.country,
                state.city,
                { callback?.onCountryClick() })
            { callback?.onCityClick() }
        })
    return filters
}