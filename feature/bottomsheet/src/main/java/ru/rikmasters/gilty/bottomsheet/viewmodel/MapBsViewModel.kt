package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.LocationModel

class MapBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()
    
    private val _last = MutableStateFlow(emptyList<LocationModel>())
    val last = _last.asStateFlow()
    
    suspend fun changeSearch(text: String) {
        _search.emit(text)
    }
    
    suspend fun getLastPlaces() {
        _last.emit(meetManager.getLastPlaces())
    }
    
    suspend fun selectPlace(
        location: LocationModel,
    ) {
        meetManager.update(
            place = location.place,
            address = location.address,
            lat = location.lat,
            lng = location.lng,
        )
    }
}