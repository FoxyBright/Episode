package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullCategoryModelList
import ru.rikmasters.gilty.presentation.model.meeting.FilterModel
import ru.rikmasters.gilty.presentation.model.meeting.FullCategoryModel
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun MeetingFilterBottomPreview() {
    GiltyTheme {
        val distanceState = remember { mutableStateOf(false) }
        val distance = remember { mutableStateOf(25) }
        val onlyOnline = remember { mutableStateOf(false) }
        val meetingTypes = remember { mutableStateListOf(false, false, false) }
        val genderList = remember { mutableStateListOf(false, false, false) }
        val conditionList = remember { mutableStateListOf(false, false, false, false, false) }
        val tagList = remember { mutableStateListOf("kaif", "pain", "fast", "launch") }
        val categoryList = remember { mutableStateOf(DemoFullCategoryModelList) }
        val categoryStateList = remember { mutableStateListOf<Boolean>() }
        repeat(categoryList.value.size) { categoryStateList.add(false) }
        val state = FilterListState(
            distanceState.value,
            distance.value,
            onlyOnline.value,
            meetingTypes,
            genderList,
            conditionList,
            tagList,
            categoryList.value,
            categoryStateList
        )
        MeetingFilterBottom(Modifier, 26, state, object : MeetingFilterBottomCallback {
            override fun onAllCategoryClick() {}
            override fun onFilterClick() {}
            override fun onCategoryClick(index: Int) {
                categoryStateList[index] = !categoryStateList[index]
            }

            override fun onDeleteTag(it: Int) {
                tagList.removeAt(it)
            }

            override fun onDistanceClick() {
                distanceState.value = !distanceState.value
            }

            override fun onDistanceValueChange(it: Int) {
                distance.value = it
            }

            override fun onOnlyOnlineClick() {
                onlyOnline.value = !onlyOnline.value
            }

            override fun onMeetingTypeSelect(it: Int, status: Boolean) {
                meetingTypes[it] = !status
            }

            override fun onGenderSelect(it: Int, status: Boolean) {
                genderList[it] = !status
            }

            override fun onConditionSelect(it: Int, status: Boolean) {
                conditionList[it] = !status
            }
        })
    }
}

@Composable
fun MeetingFilterBottom(
    modifier: Modifier = Modifier,
    find: Int? = null,
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null
) {
    Box(modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(filterList(state, callback)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        it.name,
                        Modifier.padding(top = 28.dp, bottom = 18.dp),
                        ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.H3
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
                        .padding(top = 12.dp, bottom = 28.dp)
                        .clickable { },
                    ThemeExtra.colors.mainTextColor,
                    textAlign = TextAlign.Center,
                    style = ThemeExtra.typography.Body2Bold
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
    val categoryList: List<FullCategoryModel>,
    val categoryStateList: List<Boolean>
)

interface MeetingFilterBottomCallback : NavigationInterface {
    fun onCategoryClick(index: Int)
    fun onAllCategoryClick()
    fun onFilterClick()
    fun onDeleteTag(it: Int)
    fun onDistanceClick()
    fun onDistanceValueChange(it: Int)
    fun onOnlyOnlineClick()
    fun onMeetingTypeSelect(it: Int, status: Boolean)
    fun onGenderSelect(it: Int, status: Boolean)
    fun onConditionSelect(it: Int, status: Boolean)
}

private fun filterList(
    state: FilterListState,
    callback: MeetingFilterBottomCallback? = null
): List<FilterModel> {
    return listOf(
        FilterModel("Категория") {
            Category(state.categoryList, state.categoryStateList,
                { callback?.onCategoryClick(it) },
                { callback?.onAllCategoryClick() }
            )
        },
        FilterModel("Поиск по тегам") {
            TagSearch(
                state.tagList,
                { callback?.onFilterClick() },
                { callback?.onDeleteTag(it) }
            )
        },
        FilterModel("Расстояние") {
            Distance(
                state.distance,
                state.distanceState,
                { callback?.onDistanceClick() },
                { callback?.onDistanceValueChange(it) }
            )
        },
        FilterModel("Тип встречи") {
            MeetingType(
                state.onlyOnline,
                state.meetingTypes,
                { callback?.onOnlyOnlineClick() },
                { it, status -> callback?.onMeetingTypeSelect(it, status) }
            )
        },
        FilterModel("Пол и условия") {
            GenderAndConditions(
                state.genderList,
                state.conditionList,
                { it, status -> callback?.onGenderSelect(it, status) },
                { it, status -> callback?.onConditionSelect(it, status) }
            )
        }
    )
}