package ru.rikmasters.gilty.yandexmap

import com.yandex.mapkit.MapKit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class YandexMapViewModel: ViewModel() {
    
    val mapKit = MutableStateFlow<MapKit?>(null)
    
    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()
    
    private val _meetPlace = MutableStateFlow<MeetPlace?>(null)
    val meetPlace = _meetPlace.asStateFlow()
    
    private val _place = MutableStateFlow("")
    val place = _place.asStateFlow()
    
    suspend fun changeMeetPlace(meetPlace: MeetPlace) {
        _meetPlace.emit(meetPlace)
    }
    
    suspend fun changePlace(text: String) {
        _place.emit(text)
    }
    
    suspend fun changeAddress(text: String) {
        _address.emit(text)
    }
    
    suspend fun setMapKit(map: MapKit) {
        mapKit.emit(map)
    }
}