package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun MeetingFilterBottomPreview() {
    GiltyTheme {
        MeetingFilterBottom(
            Modifier, (26), FilterListState(
                (false), (25), (false),
                listOf(false, false, false),
                listOf(false, false, false),
                listOf(false, false, false, false, false),
                listOf("kaif", "pain", "fast", "launch"),
                CategoriesType.list(),
                listOf(false, false, false),
                ("Россия"), ("Москва"),
            )
        )
    }
}

@Composable
fun MeetingFilterBottom(
    modifier: Modifier = Modifier,
    find: Int? = null,
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null
) {
    val list = filterList(state, callback)
    Box(modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(list) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        it.name,
                        Modifier.padding(top = 28.dp, bottom = 18.dp),
                        MaterialTheme.colorScheme.tertiary,
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
                    stringResource(R.string.save_button),
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
                    MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

data class FilterListState(
    val distanceState: Boolean,
    val distance: Int,
    val onlyOnline: Boolean,
    val meetingTypes: List<Boolean>,
    val genderList: List<Boolean>,
    val conditionList: List<Boolean>,
    val tagList: List<String>,
    val categoryList: List<CategoriesType>,
    val categoryStateList: List<Boolean>,
    val country: String,
    val city: String
)

interface MeetingFilterBottomCallback {
    fun onNext() {}
    fun onBack() {}
    fun onCategoryClick(index: Int) {}
    fun onAllCategoryClick() {}
    fun onFilterClick() {}
    fun onDeleteTag(it: Int) {}
    fun onDistanceClick() {}
    fun onDistanceValueChange(it: Int) {}
    fun onOnlyOnlineClick() {}
    fun onMeetingTypeSelect(it: Int, status: Boolean) {}
    fun onGenderSelect(it: Int, status: Boolean) {}
    fun onConditionSelect(it: Int, status: Boolean) {}
    fun onCountryClick() {}
    fun onCityClick() {}
    fun onClear() {}
}

@Composable
private fun filterList(
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null
): List<FilterModel> {
    return listOf(
        FilterModel(stringResource(add_meet_detailed_meet_place)) {
            Country(state.country,
                state.city,
                { callback?.onCountryClick() })
            { callback?.onCityClick() }
        },
        FilterModel(stringResource(R.string.meeting_filter_category)) {
            Category(state.categoryList,
                state.categoryStateList,
                { callback?.onCategoryClick(it) },
                { callback?.onAllCategoryClick() }
            )
        },
        FilterModel(stringResource(R.string.meeting_filter_tag_search)) {
            TagSearch(
                state.tagList,
                { callback?.onFilterClick() },
            ) { callback?.onDeleteTag(it) }
        },
        FilterModel(stringResource(R.string.meeting_filter_distance)) {
            Distance(
                state.distance,
                state.distanceState,
                { callback?.onDistanceClick() },
                { callback?.onDistanceValueChange(it) }
            )
        },
        FilterModel(stringResource(R.string.meeting_filter_meet_type)) {
            MeetingType(
                state.onlyOnline,
                state.meetingTypes,
                stringResource(R.string.meeting_only_online_meetings_button),
                (false), { callback?.onOnlyOnlineClick() },
                { it, status ->
                    callback?.onMeetingTypeSelect(it, status)
                }
            )
        },
        FilterModel(stringResource(R.string.meeting_filter_gender_and_conditions)) {
            GenderAndConditions(
                state.genderList,
                state.conditionList,
                { it, status ->
                    callback?.onGenderSelect(it, status)
                }, { it, status ->
                    callback?.onConditionSelect(it, status)
                }
            )
        }
    )
}