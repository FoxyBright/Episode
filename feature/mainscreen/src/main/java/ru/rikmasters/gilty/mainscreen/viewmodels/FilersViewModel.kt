package ru.rikmasters.gilty.mainscreen.viewmodels

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.auth.meetings.MeetFilters
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource.MeetFilterGroup.Companion.get
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

@OptIn(FlowPreview::class)
class FiltersViewModel(
    
    val mainVm: MainViewModel = MainViewModel(),
): ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _screen = MutableStateFlow(0)
    val screen = _screen.asStateFlow()
    
    private val _distance = MutableStateFlow(15)
    val distance = _distance.asStateFlow()
    
    @Suppress("unused")
    val distanceDebounced = distance
        .debounce(250)
        .onEach { findMeets() }
        .state(_distance.value, SharingStarted.Eagerly)
    
    private val _distanceState = MutableStateFlow(false)
    val distanceState = _distanceState.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    private val _selectedGenders = MutableStateFlow(emptyList<Int>())
    val selectedGenders = _selectedGenders.asStateFlow()
    
    private val _selectedCondition = MutableStateFlow(emptyList<Int>())
    val selectedCondition = _selectedCondition.asStateFlow()
    
    private val _meetTypes = MutableStateFlow(emptyList<Int>())
    val meetTypes = _meetTypes.asStateFlow()
    
    private val _country = MutableStateFlow("")
    val country = _country.asStateFlow()
    
    private val _city = MutableStateFlow("")
    val city = _city.asStateFlow()
    
    private val _results = MutableStateFlow<Int?>(null)
    val results = _results.asStateFlow()
    
    private val _myInterest =
        MutableStateFlow(emptyList<CategoryModel>())
    val myInterest = _myInterest.asStateFlow()
    
    private val _allCategories =
        MutableStateFlow(emptyList<CategoryModel>())
    val allCategories = _allCategories.asStateFlow()
    
    private val _selectedCategories =
        MutableStateFlow(emptyList<CategoryModel>())
    val selectedCategories = _selectedCategories.asStateFlow()
    
    private val _selectedAdditionally =
        MutableStateFlow(emptyList<CategoryModel>())
    val selectedAdditionally = _selectedAdditionally.asStateFlow()
    
    private val _categories =
        MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    private val _categoriesStates = MutableStateFlow(emptyList<Int>())
    val categoriesStates = _categoriesStates.asStateFlow()
    
    private val _additionallyStates = MutableStateFlow(emptyList<Int>())
    val additionallyStates = _additionallyStates.asStateFlow()
    
    private val _tags = MutableStateFlow(emptyList<TagModel>())
    val tags = _tags.asStateFlow()
    
    private val _additionallyTags = MutableStateFlow(emptyList<TagModel>())
    val additionallyTags = _additionallyTags.asStateFlow()
    
    private val _tagSearch = MutableStateFlow("")
    val tagSearch = _tagSearch.asStateFlow()
    
    private val _popularTags = MutableStateFlow(emptyList<TagModel>())
    val popularTags = _popularTags.asStateFlow()
    
    private val _popularVisible = MutableStateFlow(true)
    val popularVisible = _popularVisible.asStateFlow()
    suspend fun getAllCategories() {
        _allCategories.emit(meetManager.getCategoriesList())
    }
    
    suspend fun getUserCategories() {
        _myInterest.emit(removeChildren(meetManager.getUserCategories()))
    }
    
    fun removeChildren(categories: List<CategoryModel>) =
        categories.filter { my ->
            !allCategories.value
                .filter { it.children != null }
                .flatMap { it.children!! }.contains(my)
        }
    
    suspend fun changeCategoryState(category: Int) {
        val list = categoriesStates.value
        _categoriesStates.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
    }
    
    suspend fun changeAdditionallyStates(category: Int) {
        val list = additionallyStates.value
        _additionallyStates.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
    }
    
    suspend fun fullAdditionallySelect() {
        _selectedAdditionally.emit(selectedCategories.value)
    }
    
    suspend fun selectCategory(category: CategoryModel) {
        val list = selectedCategories.value
        _selectedCategories.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
        
        _categories.emit(removeChildren(selectedCategories.value))
        findMeets()
    }
    
    suspend fun selectAdditionally(category: CategoryModel) {
        val list =
            selectedAdditionally.value
        _selectedAdditionally.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
    }
    
    suspend fun clearAdditionally() {
        _selectedAdditionally.emit(emptyList())
        _additionallyStates.emit(emptyList())
    }
    
    suspend fun onAddComplete() {
        _selectedCategories.emit(selectedAdditionally.value)
        _categories.emit(removeChildren(selectedCategories.value))
        findMeets()
    }
    
    suspend fun findMeets() {
        _results.emit(meetManager.getMeetCount(filtersBuilder()))
    }
    
    private fun filtersBuilder() = MeetFilters(
        group = get(mainVm.today.value.compareTo(false)),
        categories = selectedCategories.value.ifEmpty { null },
        tags = tags.value.ifEmpty { null },
        radius = if(mainVm.today.value) distance.value else null,
        lat = if(mainVm.today.value) 0 else null,
        lng = if(mainVm.today.value) 0 else null,
        onlyOnline = online.value,
        meetTypes = if(meetTypes.value.isNotEmpty()) {
            meetTypes.value.map { MeetType.get(it) }
        } else null,
        conditions = if(selectedCondition.value.isNotEmpty())
            selectedCondition.value.map { ConditionType.get(it) }
        else null,
        genders = if(selectedGenders.value.isNotEmpty())
            selectedGenders.value.map { GenderType.get(it) }
        else null,
        time = mainVm.time.value.ifBlank { null },
        dates = mainVm.days.value.ifEmpty { null }
    )
    
    suspend fun navigate(page: Int) {
        _screen.emit(page)
    }
    
    suspend fun changeCountry(country: String) {
        _country.emit(country)
        makeToast("Стран пока нет")
        findMeets()
    }
    
    suspend fun changeCity(city: String) {
        _city.emit(city)
        makeToast("Городов пока нет")
        findMeets()
    }
    
    suspend fun selectMeetType(meetType: Int) {
        val list = meetTypes.value
        _meetTypes.emit(
            if(list.contains(meetType))
                list - meetType
            else list + meetType
        )
        findMeets()
    }
    
    suspend fun clearFilters() {
        _country.emit("")
        _city.emit("")
        _selectedCategories.emit(emptyList())
        _tags.emit(emptyList())
        _distance.emit(15)
        _meetTypes.emit(emptyList())
        _online.emit(false)
        _selectedGenders.emit(emptyList())
        _selectedCondition.emit(emptyList())
        findMeets()
        mainVm.setFilters(filtersBuilder())
        mainVm.getMeets()
    }
    
    suspend fun selectGender(gender: Int) {
        val list = _selectedGenders.value
        _selectedGenders.emit(
            if(list.contains(gender))
                list - gender
            else list + gender
        )
        findMeets()
    }
    
    suspend fun selectCondition(condition: Int) {
        val list = selectedCondition.value
        _selectedCondition.emit(
            if(list.contains(condition))
                list - condition
            else list + condition
        )
        findMeets()
    }
    
    suspend fun changeDistanceState() {
        _distanceState.emit(!distanceState.value)
    }
    
    suspend fun changeOnline() {
        _online.emit(!online.value)
        findMeets()
    }
    
    suspend fun onSave() {
        mainVm.setFilters(filtersBuilder())
        mainVm.getMeets()
    }
    
    suspend fun changeDistance(distance: Int) {
        _distance.emit(distance)
    }
    
    private val _searchResult = MutableStateFlow(emptyList<TagModel>())
    val searchResult = _searchResult
        .combine(tagSearch.debounce(250)) { _, current ->
            meetManager.searchTags(current)
        }.state(_searchResult.value)
    
    suspend fun getPopularTags() {
        _popularTags.emit(tags.value + meetManager
            .getPopularTags(selectedCategories.value.map { it.id })
            .filter { !tags.value.contains(it) }
        )
    }
    
    suspend fun clearTagSearch() {
        _tagSearch.emit("")
        _popularVisible.emit(true)
    }
    
    suspend fun addToPopular(tag: TagModel) {
        _popularTags.emit(popularTags.value + tag)
    }
    
    suspend fun deleteTag(tag: TagModel) {
        _tags.emit(tags.value - tag)
        findMeets()
    }
    
    suspend fun deleteAdditionallyTag(tag: TagModel) {
        _additionallyTags.emit(additionallyTags.value - tag)
    }
    
    suspend fun selectTag(tag: TagModel) {
        val list = additionallyTags.value
        _additionallyTags.emit(
            if(list.contains(tag))
                list - tag
            else list + tag
        )
    }
    
    suspend fun fullAdditionallyTags() {
        _additionallyTags.emit(tags.value)
    }
    
    suspend fun searchTags(text: String) {
        _popularVisible.emit(text.isBlank())
        _tagSearch.emit(text)
    }
    
    suspend fun saveTags() {
        _tags.emit(additionallyTags.value)
        findMeets()
    }
}