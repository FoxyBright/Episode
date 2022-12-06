package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ru.rikmasters.gilty.mainscreen.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.shared.common.tagSearch.TagSearchScreen
import ru.rikmasters.gilty.shared.model.meeting.DemoFullCategoryModelList

@Composable
fun MeetingFilterContent(onSave: () -> Unit) {
    val distanceState =
        remember { mutableStateOf(false) }
    val distance =
        remember { mutableStateOf(25) }
    val onlyOnline =
        remember { mutableStateOf(false) }
    val meetingTypes =
        remember { mutableStateListOf(false, false, false) }
    val genderList =
        remember { mutableStateListOf(false, false, false) }
    val conditionList =
        remember { mutableStateListOf(false, false, false, false, false) }
    val tagList =
        remember { mutableStateListOf("kaif", "pain", "fast", "launch") }
    val categoryList =
        remember { mutableStateOf(DemoFullCategoryModelList) }
    val categoryStateList =
        remember { mutableStateListOf<Boolean>() }
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
    
    val context = LocalContext.current
    
    var categories by remember { mutableStateOf(0) }
    
    when(categories) {
        1 -> CategoriesScreen { _, _ -> categories = 0 }
        2 -> TagSearchScreen(false, { categories = 0 }) { categories = 0 }
        else -> MeetingFilterBottom(Modifier, 26, state,
            object: MeetingFilterBottomCallback {
                override fun onAllCategoryClick() {
                    categories = 1
                }
                
                 override fun onFilterClick() {
                    categories = 2
                }
                
                override fun onCategoryClick(index: Int) {
                    categoryStateList[index] = !categoryStateList[index]
                }
                
                override fun onDeleteTag(it: Int) {
                    tagList.removeAt(it)
                }
                
                override fun onClear() {
                    Toast.makeText(
                        context,
                        "Фильтры сброшены",
                        Toast.LENGTH_SHORT
                    ).show()
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
                
                override fun onNext() {
                    onSave()
                }
                
                override fun onConditionSelect(it: Int, status: Boolean) {
                    conditionList[it] = !status
                }
            })
    }
}