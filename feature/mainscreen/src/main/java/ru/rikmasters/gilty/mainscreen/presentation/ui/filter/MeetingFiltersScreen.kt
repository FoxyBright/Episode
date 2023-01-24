package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ru.rikmasters.gilty.mainscreen.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.shared.common.tagSearch.TagSearchScreen
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel

@Composable
fun MeetingFilterContent(
    onSave: () -> Unit
) {
    val distanceState =
        remember { mutableStateOf(false) }
    val distance = remember { mutableStateOf(25) }
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
        remember { mutableStateOf(listOf(DemoCategoryModel)) }
    val categoryStateList =
        remember { mutableStateListOf<Boolean>() }
    repeat(categoryList.value.size) { categoryStateList.add(false) }
    val country = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val state = FilterListState(
        distanceState.value, distance.value,
        onlyOnline.value, meetingTypes,
        genderList, conditionList,
        tagList, categoryList.value,
        categoryStateList, country.value,
        city.value
    )
    
    val context = LocalContext.current
    
    var bottomSheetScreen by remember { mutableStateOf(0) }
    
    when(bottomSheetScreen) {
        1 -> CategoriesScreen { _, _ ->
            bottomSheetScreen = 0
        }
        
        2 -> TagSearchScreen(false,
            { bottomSheetScreen = 0 })
        { bottomSheetScreen = 0 }
        
        //        3 -> {}
        //
        //        4 -> {}
        
        else -> MeetingFilterBottom(Modifier, 26, state,
            object: MeetingFilterBottomCallback {
                override fun onAllCategoryClick() {
                    bottomSheetScreen = 1
                }
                
                override fun onFilterClick() {
                    bottomSheetScreen = 2
                }
                
                override fun onCountryClick() {
                    country.value = "Россия"
                    //                    bottomSheetScreen = 3
                }
                
                override fun onCityClick() {
                    city.value = "Москва"
                    //                    bottomSheetScreen = 4
                }
                
                override fun onCategoryClick(index: Int) {
                    categoryStateList[index] = !categoryStateList[index]
                }
                
                override fun onDeleteTag(it: Int) {
                    tagList.removeAt(it)
                }
                
                override fun onClear() {
                    Toast.makeText( //TODO - сделать сброс фильтров
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