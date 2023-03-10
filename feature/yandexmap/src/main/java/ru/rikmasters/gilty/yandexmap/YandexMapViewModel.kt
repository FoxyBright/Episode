package ru.rikmasters.gilty.yandexmap

import com.yandex.mapkit.MapKit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class YandexMapViewModel: ViewModel() {
    
    val mapKit = MutableStateFlow<MapKit?>(null)
    
    private val _userLocation = MutableStateFlow(true)
    val userLocation = _userLocation.asStateFlow()
    
    suspend fun setMapKit(map: MapKit) {
        mapKit.emit(map)
    }
}