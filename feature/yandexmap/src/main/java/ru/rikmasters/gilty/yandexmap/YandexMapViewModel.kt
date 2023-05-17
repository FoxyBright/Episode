package ru.rikmasters.gilty.yandexmap

import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager

class YandexMapViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    val addMeet by lazy { manager.addMeetFlow.state(null) }
    
    val mapKit = MutableStateFlow<MapKit?>(null)

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _currentPoint = MutableStateFlow<Point?>(null)
    val currentPoint = _currentPoint.asStateFlow()

    val isSearchingDebounced = currentPoint
        .debounce(500)
        .onEach {
            _isSearching.emit(false)
        }
        .state(_isSearching.value, SharingStarted.Eagerly)

    suspend fun changeMeetPlace(meetPlace: MeetPlace) {
        manager.update(
            place = meetPlace.place,
            address = meetPlace.address,
            lat = meetPlace.lat,
            lng = meetPlace.lng,
        )
    }
    suspend fun setMapKit(map: MapKit) {
        mapKit.emit(map)
    }
    suspend fun onIsSearchingChange(value:Boolean){
        delay(1000L)
        _isSearching.emit(value)
    }

    suspend fun onCameraChange(point:Point) {
        _isSearching.emit(true)
        _currentPoint.emit(point)
    }
}