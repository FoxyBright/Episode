package ru.rikmasters.gilty.mainscreen.viewmodels.bottoms

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.*
import ru.rikmasters.gilty.shared.model.enumeration.MeetFilterGroupType.Companion.get
import ru.rikmasters.gilty.shared.model.meeting.*

@OptIn(FlowPreview::class)
class FiltersBsViewModel(
    val mainVm: MainViewModel = MainViewModel(),
): ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _screen = MutableStateFlow(0)
    val screen = _screen.asStateFlow()
    
    private val _distance = MutableStateFlow(15)
    val distance = _distance.asStateFlow()
    
    @Suppress("unused")
    val distanceDebounced = distance
        .debounce(250)
        .onEach { findMeets() }
        .state(_distance.value, SharingStarted.Eagerly)
    
    private val _distanceState =
        MutableStateFlow(false)
    val distanceState =
        _distanceState.asStateFlow()
    
    private val _isOnline =
        MutableStateFlow(false)
    val isOnline =
        _isOnline.asStateFlow()
    
    private val _selectedCondition =
        MutableStateFlow(emptyList<Int>())
    val selectedCondition =
        _selectedCondition.asStateFlow()
    
    private val _meetTypes =
        MutableStateFlow(emptyList<Int>())
    val meetTypes =
        _meetTypes.asStateFlow()
    
    private val _city =
        MutableStateFlow<CityModel?>(null)
    val city =
        _city.asStateFlow()
    
    private val _cityList =
        MutableStateFlow(emptyList<CityModel>())
    val cityList =
        _cityList.asStateFlow()
    
    private val _searchCity =
        MutableStateFlow("")
    val searchCity =
        _searchCity.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val searchCitiesDebounced = searchCity
        .debounce(250)
        .onEach { getCities() }
        .state(_distance.value, SharingStarted.Eagerly)
    
    private val _searchCityState =
        MutableStateFlow(false)
    val searchCityState =
        _searchCityState.asStateFlow()
    
    private val _results =
        MutableStateFlow<Int?>(null)
    val results =
        _results.asStateFlow()
    
    private val _myInterest =
        MutableStateFlow(emptyList<CategoryModel>())
    val myInterest =
        _myInterest.asStateFlow()
    
    private val _allCategories =
        MutableStateFlow(emptyList<CategoryModel>())
    val allCategories =
        _allCategories.asStateFlow()
    
    private val _selectedCategories =
        MutableStateFlow(emptyList<CategoryModel>())
    val selectedCategories =
        _selectedCategories.asStateFlow()
    
    private val _selectedAdditionally =
        MutableStateFlow(emptyList<CategoryModel>())
    val selectedAdditionally =
        _selectedAdditionally.asStateFlow()
    
    private val _categories =
        MutableStateFlow(emptyList<CategoryModel>())
    val categories =
        _categories.asStateFlow()
    
    private val _categoriesStates =
        MutableStateFlow(emptyList<Int>())
    val categoriesStates =
        _categoriesStates.asStateFlow()
    
    private val _additionallyStates =
        MutableStateFlow(emptyList<Int>())
    val additionallyStates =
        _additionallyStates.asStateFlow()
    
    private val _tags =
        MutableStateFlow(emptyList<TagModel>())
    val tags = _tags.asStateFlow()
    
    private val _additionallyTags =
        MutableStateFlow(emptyList<TagModel>())
    val additionallyTags =
        _additionallyTags.asStateFlow()
    
    private val _tagSearch =
        MutableStateFlow("")
    val tagSearch = _tagSearch.asStateFlow()
    
    private val _popularTags =
        MutableStateFlow(emptyList<TagModel>())
    val popularTags =
        _popularTags.asStateFlow()
    
    suspend fun changeSearchCountryState(state: Boolean) {
        _searchCityState.emit(state)
    }
    
    suspend fun getCities() {
        _cityList.emit(
            meetManager.getCities(
                searchCity.value
            )
        )
    }
    
    suspend fun changeSearchQuery(query: String) {
        _searchCity.emit(query)
    }
    
    suspend fun getAllCategories() {
        _allCategories.emit(meetManager.getCategoriesList())
    }
    
    suspend fun getUserCategories() {
        _myInterest.emit(
            removeChildren(
                profileManager.getUserCategories()
            )
        )
    }
    
    fun removeChildren(categories: List<CategoryModel>) =
        categories.filter { my ->
            !allCategories.value
                .filter { it.children != null }
                .flatMap { it.children ?: emptyList() }.contains(my)
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
        val result by lazy {
            val list = selectedAdditionally.value
            category.parentId?.let { parentId ->
                val set = setOf(category,
                    allCategories.value.first { it.id == parentId })
                
                (if(list.contains(category)) list - set
                else list + set).distinct()
                
            } ?: run {
                if(list.contains(category))
                    list - category
                else list + category
            }
        }
        _selectedAdditionally.emit(result)
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
    
    val hasFilters = mainVm.meetFilters
    
    suspend fun findMeets() = singleLoading {
        _results.emit(
            meetManager.getMeetCount(
                filtersBuilder().copy(
                    genders = listOf(
                        profileManager
                            .getProfile()
                            .gender
                    )
                )
            )
        )
    }
    
    private fun filtersBuilder() = MeetFiltersModel(
        group = get(mainVm.today.value.compareTo(false)),
        categories = selectedCategories.value.ifEmpty { null },
        tags = tags.value.ifEmpty { null },
        radius = if(mainVm.today.value)
            (distance.value * 1000) else null,
        lat = if(mainVm.today.value) 0 else null,
        lng = if(mainVm.today.value) 0 else null,
        onlyOnline = isOnline.value,
        meetTypes = if(meetTypes.value.isNotEmpty()) {
            meetTypes.value.map { MeetType.get(it) }
        } else null,
        conditions = if(selectedCondition.value.isNotEmpty())
            selectedCondition.value.map { ConditionType.get(it) }
        else null,
        time = mainVm.time.value.ifBlank { null },
        dates = mainVm.days.value.ifEmpty { null },
        city = city.value
    )
    
    suspend fun navigate(page: Int) {
        _screen.emit(page)
    }
    
    suspend fun changeCity(city: CityModel) {
        _city.emit(city)
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
        _city.emit(null)
        _selectedCategories.emit(emptyList())
        _tags.emit(emptyList())
        _distance.emit(15)
        _meetTypes.emit(emptyList())
        _isOnline.emit(false)
        _selectedCondition.emit(emptyList())
        mainVm.moreMeet()
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
        _isOnline.emit(!isOnline.value)
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
        _popularTags.emit(
            meetManager.getPopularTags(
                selectedCategories.value.map { it.id }
            )
        )
    }
    
    suspend fun clearTagSearch() {
        _tagSearch.emit("")
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
        _tagSearch.emit(text)
    }
    
    suspend fun saveTags() {
        _tags.emit(additionallyTags.value)
        findMeets()
    }
}