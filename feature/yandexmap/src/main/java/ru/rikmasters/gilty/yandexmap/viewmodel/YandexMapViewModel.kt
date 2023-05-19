package ru.rikmasters.gilty.yandexmap.viewmodel

import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.yandexmap.model.MeetPlaceModel

class YandexMapViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    val addMeet by lazy {
        manager.addMeetFlow.state(null)
    }
    
    val mapKit =
        MutableStateFlow<MapKit?>(null)
    
    private val _isSearching =
        MutableStateFlow(false)
    val isSearching =
        _isSearching.asStateFlow()
    
    private val _currentPoint =
        MutableStateFlow<Point?>(null)
    
    private val currentPoint =
        _currentPoint.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val isSearchingDebounced = currentPoint
        .debounce(500)
        .onEach { _isSearching.emit(false) }
        .state(_isSearching.value, Eagerly)
    
    suspend fun changeMeetPlace(
        meetPlaceModel: MeetPlaceModel,
    ) {
        manager.update(
            place = meetPlaceModel.place,
            address = meetPlaceModel.address,
            lat = meetPlaceModel.lat,
            lng = meetPlaceModel.lng,
        )
    }
    
    suspend fun setMapKit(map: MapKit) {
        mapKit.emit(map)
    }
    
    suspend fun onIsSearchingChange(value: Boolean) {
        delay(1000L)
        _isSearching.emit(value)
    }
    
    suspend fun onCameraChange(point: Point) {
        _isSearching.emit(true)
        _currentPoint.emit(point)
    }
}