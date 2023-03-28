package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager

class MapBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()
    
    private val _last = MutableStateFlow(emptyList<Pair<String, String>>())
    val last = _last.asStateFlow()
    
    suspend fun changeSearch(text: String) {
        _search.emit(text)
    }
    
    suspend fun getLastPlaces() {
        _last.emit(meetManager.getLastPlaces())
    }
    
    suspend fun selectPlace(place: Pair<String, String>) {
//        detailedVm.changePlace(place)
//        Address = place.first
//        Place = place.second
    }
}